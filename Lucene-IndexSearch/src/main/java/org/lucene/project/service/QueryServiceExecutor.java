package org.lucene.project.service;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.lucene.project.utils.AppConstants;
import org.lucene.project.utils.AppHelper;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class QueryServiceExecutor implements IServiceExecutor{

    @Override
    public void executeCommand(String command) {

        List<String> commandList = AppHelper.tokenizeCommand(command);
        if(AppHelper.foundNotAlphaNumeric(commandList.subList(1, commandList.size()), AppConstants.QUERY_COMMAND)){
            System.err.println("query error: token is not alphanumeric");
            return;
        }

        String[] tokens = command.split(" ");
        if (tokens.length == 3){
            System.err.println("query error: command not properly");
            return;
        }
        if (tokens.length > 4 && (!command.contains("(") || !command.contains(")"))) {
            System.err.println("query error: command not properly. Parentheses mandatory");
            return;
        }
        for(int i=2; i<tokens.length; i=i+2) {
            if (!tokens[i].equals("&") && !tokens[i].equals("|")){
                System.err.println("query error: command not properly. Logical Operator mandatory");
                return;
            }
        }

        String query = command.substring(6);
        String modifiedQuery = query.replace("&", "AND").replace("|", "OR");
        searchToken(modifiedQuery);

    }

    private void searchToken(String query){
        try {
            Directory directory = FSDirectory.open(Paths.get(AppConstants.INDEX_PATH));
            IndexReader reader = DirectoryReader.open(directory);
            IndexSearcher searcher = new IndexSearcher(reader);
            Query queryParser = new QueryParser("contents", new StandardAnalyzer()).parse(query);
            long start = System.currentTimeMillis();
            TopDocs hits = searcher.search(queryParser, 10);
            long end = System.currentTimeMillis();
            System.out.println("query results:  " + hits.totalHits + " document(s) (in " + (end - start) + " milliseconds) that matched query '" + query);
            for(ScoreDoc scoreDoc : hits.scoreDocs) {
                Document document = searcher.doc(scoreDoc.doc);
                System.out.println(document.get("fileName"));
            }
            reader.close();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }
}
