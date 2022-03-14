package org.lucene.project.service;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.lucene.project.utils.AppConstants;
import org.lucene.project.utils.AppHelper;

import java.io.*;
import java.nio.file.Paths;
import java.util.List;

public class IndexServiceExecutor implements IServiceExecutor{

    private IndexWriter writer;

    @Override
    public void executeCommand(String command) {

        List<String> commandList = AppHelper.tokenizeCommand(command);

        if(!AppHelper.isNumeric(commandList.get(1))){
            System.err.println("index error: doc id is not an integer");
            return;
        }

        if(AppHelper.foundNotAlphaNumeric(commandList.subList(1, commandList.size()), AppConstants.INDEX_COMMAND)){
            System.err.println("index error: token is not alphanumeric");
            return;
        }


        String fileName = AppConstants.FILE_PATH + commandList.get(0) + commandList.get(1) + AppConstants.FILE_TYPE;
        createFile(fileName, command);
        try {
            indexDocument(fileName, commandList.get(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createFile(String fileName, String command) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(fileName);
            fileWriter.write(command.substring(8));
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void indexDocument(String fileName, String docId) throws IOException{
        long start = System.currentTimeMillis();
        try {
            Directory directory = FSDirectory.open(Paths.get(AppConstants.INDEX_PATH));
            IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
            this.writer = new IndexWriter(directory, config);
            index(fileName, new TextFilesFilter());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
        long end = System.currentTimeMillis();
        System.out.println("index ok " + docId + ". (Time taken: " + (end - start) + " milliseconds.)");
    }

    //Close IndexWriter
    public void close() throws IOException {
        this.writer.close();
    }

    // Index .txt files only, using FileFilter
    private static class TextFilesFilter implements FileFilter {
        public boolean accept(File path) {
            return path.getName().toLowerCase().endsWith(".txt");
        }
    }

    //Return number of documents indexed
    public void index(String dataDirectory, FileFilter filter) throws Exception {
        File file = new File(dataDirectory);
        if (!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && (filter == null || filter.accept(file)))
            indexFile(file);
    }

    //Add document to Lucene Index
    private void indexFile(File file) throws Exception {
        Document document = getDocument(file);
        this.writer.addDocument(document);
    }

    private Document getDocument(File file) throws Exception {
        Document document = new Document();
        document.add(new TextField("contents", new FileReader(file)));
        document.add(new TextField("fileName", file.getName(), Field.Store.YES)); //Index file name
        return document;
    }

}
