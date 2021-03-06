package Indexer.IndexBuilder;

import Indexer.Enums.CompressionLevel;
import Indexer.Models.Collection;
import Indexer.Models.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.Map.Entry;


public class Spimi {
	public static void run (Collection collection, CompressionLevel level) {
		//trigger garbage collection before getting initial memory
		System.gc();
		CollectionStatistics stats = new CollectionStatistics();
		long initialMemory = java.lang.Runtime.getRuntime().freeMemory();
		long blockSizeLimit = 200;
		Index index = new Index();
		int blockCount = 0;
		int docWordCount = 0;
		//Sentiment analysis of documents
		Map<String, Integer> sentimentValues = new HashMap<String, Integer>();
		
		//loop every document in reuters
		System.out.println("Tokenizing, compressing, and creating blocks from collection...");
		for (DocumentArticle document : collection.getDocuments()) {
			Document doc = createDocument(level, document);
			
			//loop every token in document
			for(String term : doc.getTokens()) {
				long currentMemory = java.lang.Runtime.getRuntime().freeMemory();
				long usedMemory = initialMemory - currentMemory;
				
				//check if memory block size limit is reached
				if ((usedMemory/1024/1024) > blockSizeLimit) {
					//write block, free memory, create new index
					blockCount = writeBlock(index, blockCount);
					index = null;
					System.gc();
					index = new Index();
				}
				
				//insert posting
				index.insert(doc.getId(), term, 1);
				docWordCount++;
			}
			//keep track of doc sentiment values
			sentimentValues.put(doc.getTitle(), doc.analyzeSentiment());
			//keep track of doc lengths
			stats.insert(doc.getId(), docWordCount);
			docWordCount = 0;
		}
		
		
		//if final index has data write the final block
		if(!index.isEmpty()){
			blockCount = writeBlock(index, blockCount);
			index = null;
			System.gc();
			index = new Index();
		}
		stats.writeStatistics(level);
		writeSentiment(sentimentValues, level);
	}

	private static Document createDocument(CompressionLevel level, DocumentArticle document) {
		Document doc = new Document();

		//build document with ID and compressed tokens
		doc.setId(document.getId());
		doc.setTitle(document.getContent().getTitle());
		doc.setTokens(Compress.compress(tokenize(document.getContent().getBody()), level));
		return doc;
	}

	private static int writeBlock(Index index, int blockCount) {
		BlockManager.writeBlock(blockCount, index);
		blockCount++;
		return blockCount;
	}

	public static List<String> tokenize(String content) {
		List<String> tokens = new ArrayList<String>();
		
		//check if document is empty
		if(content != null) {
			//tokenize on newlines, / and spaces
			StringTokenizer tokenizer = new StringTokenizer(content, "\n/ ");
			
			//create token list
			while (tokenizer.hasMoreTokens()) {
				tokens.add(tokenizer.nextToken());
			}
		}
		
		return tokens;
	}
	
	private static void writeSentiment(Map<String, Integer> SV, CompressionLevel level){
		//write sentiment values to disk
		try {
			Writer writer = new FileWriter(System.getProperty("user.dir") + "/src/sentiment/sentimentValues" + level);

			StringBuffer sb = new StringBuffer();
			//write each term and posting list to a new line in the block
			Iterator<Entry<String, Integer>> it = SV.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry<String, Integer> pair = (Map.Entry<String, Integer>)it.next();
		        sb.append("[" + pair.getKey() + "," + pair.getValue() + "]\n");
		    }
			
			writer.write(sb.toString());
			writer.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
