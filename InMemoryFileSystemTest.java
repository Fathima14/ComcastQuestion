
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import com.comcast.file.InMemoryFileSystem;
import com.comcast.file.InMemoryFileSystem.FileSystemException;
import com.comcast.file.InMemoryFileSystem.commands;

@PrepareForTest(commands.class)
public class InMemoryFileSystemTest  extends TestCase{

	InMemoryFileSystem fileSystem;
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	
	@Before
	public void setUp() {
		fileSystem = new InMemoryFileSystem();
		System.setOut(new PrintStream(outContent));
	}
	
	@Test
	public void test() {}

	@Test
	public void testUserChoice() {
		int userChoice = fileSystem.userChoice(new Scanner("1"));
		assertEquals(1, userChoice);
	}
	
	@Test(expected = FileSystemException.class)
	public void testReadUserInput() throws FileSystemException  {
		String[] input = fileSystem.readUserInput(new Scanner("mkdir"));
		String[] expected = new String[] {"mkdir"};
		assertEquals(expected[0], input[0]);
	}
	
	@Test
	public void testExtractFileName(){
		String result = fileSystem.extractFileName("/folder1/folder2/file.txt");
		assertEquals("/file.txt", result);
	}

	@Test
	public void testExtractFolderName(){
		String result = fileSystem.extractFolderName("/folder1/folder2");
		assertEquals("folder1", result);
	}
	
	@Test
	public void testIsLeafFolder() {
		boolean result = fileSystem.isLeafFolder("folder1/folder2");
		assertEquals(true, result);
	}

	@Test
	public void testIsLeafFolder1() {
		boolean result1 = fileSystem.isLeafFolder("folder1");
		assertEquals(false, result1);
	}
	
	@Test
	public void testPutFolderName() {
		 Map<String, Map<String, String>> fileSystemMap = new HashMap<String, Map<String,String>>();
		 fileSystem.putFolderName(fileSystemMap, "folder1");
		 assertEquals(1, fileSystemMap.size());
	}
	
	@Test
	public void testPutFolderName1() {
		 Map<String, Map<String, String>> fileSystemMap = new HashMap<String, Map<String,String>>();
		 fileSystem.putFolderName(fileSystemMap, "folder1");
		 String result = fileSystemMap.keySet().iterator().next();
		 assertEquals("folder1", result);
		 
	}
	
	@Test
	public void testPrintFolderNamesAndContents() {
		 Map<String, Map<String, String>> fileSystemMap = new HashMap<String, Map<String,String>>();
		 fileSystem.printFolderNamesAndContents(fileSystemMap, "test");
		 assertEquals("File not found and file system is empty", outContent.toString()); 
	}
	
	@Test
	public void testPrintFolderNamesAndContents1() {
		 Map<String, Map<String, String>> fileSystemMap = new HashMap<String, Map<String,String>>();
		 fileSystem.putFolderName(fileSystemMap, "folder1");
		 fileSystem.printFolderNamesAndContents(fileSystemMap, "folder1");
		 StringBuilder result = new StringBuilder("Root Folder name : folder1");
		 assertEquals(true, outContent.toString().contains(result)); 
	}
	
	@Test
	public void testWriteFileContent(){
		Map<String, Map<String, String>> fileSystemMap = new HashMap<String, Map<String,String>>();
		fileSystem.putFolderName(fileSystemMap, "folder1");
		fileSystem.writeFileContent(fileSystemMap, "testdata", "test.txt", "folder1");
		fileSystem.printFolderNamesAndContents(fileSystemMap, "folder1");
		StringBuilder result = new StringBuilder("File Name : ").append("test.txt").append(" File Content :").append("testdata");
		assertEquals(true, outContent.toString().contains(result)); 
	}
	
	@After
	public void cleanUpStreams() {
	    System.setOut(null);
	}
}
