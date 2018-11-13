package pt.iflow.api.search.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class Indexer {
	
	static final String PROCDATA = "PROCDATA";
	static final String PID = "PID";
	static final String FID = "FID";
	
	private static Directory index=null;
	private static IndexWriter indexWriter = null;
	private static StandardAnalyzer analyzer = null;
	
	public static void startIndexer () throws IOException{
		if(index==null){
			analyzer = new StandardAnalyzer(Version.LUCENE_45);
			Directory index = new RAMDirectory();	
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_45,analyzer);
			indexWriter = new IndexWriter(index, config);
		}
	}
	
	public static void addProcess (String flowid, String pid, String procdata) throws IOException{
		Document doc = new Document();
		doc.add(new TextField(PROCDATA, procdata, Field.Store.YES));
		doc.add(new StringField(PID, pid, Field.Store.YES));
		doc.add(new StringField(FID, flowid, Field.Store.YES));		
		if(indexWriter!=null)
			indexWriter.addDocument(doc);
	}


	public static String[] search(String querystr, Integer hitsPerPage) throws ParseException, IOException{
		Query q = new QueryParser(Version.LUCENE_45, PROCDATA, analyzer).parse(querystr);		
		IndexReader reader = IndexReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		
		String []result = new String[hits.length];
		for(int i=0;i<hits.length;++i) {
		    int docId = hits[i].doc;
		    Document d = searcher.doc(docId);
		    result[i] = d.get(PID);
		}
		return result;
	}

}
