package com.comcast.file;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;


public class InMemoryFileSystem {

	public Map<String, Map<String, String>> fileSystem = new HashMap<String, Map<String,String>>();
	public static enum commands { mkdir, create, write, cat, cp, find, ls};
	
	public static void main(String[] args) {
		InMemoryFileSystem fileSystem = new InMemoryFileSystem();
		fileSystem.manageFileSystem();
	}
	
	private void printMenu(){
		System.out.println();
		System.out.println("Welcome to In-Memory File System ");
		System.out.println("Press 1 to Create a new folder , ex: mkdir /folder1");
		System.out.println("Press 2 to Create a new file , ex: create /file1 , create /folder1/file1");
		System.out.println("Press 3 to Add content to a file , ex: write \"data1\" /file1 ");
		System.out.println("Press 4 to Display file contents , ex : cat /file1");
		System.out.println("Press 5 to List folder contents ex: find /file1 ");
		System.out.println("Press 6 to List folder contents ex:  ls file1");
		System.out.println("Press 9 to quit");
		
	} 
	public void manageFileSystem(){
		Scanner br = new Scanner(System.in);
		
		while(true) {
		try {
			int choice = userChoice(br);	
			switch(choice){
						case 1: {
								String[] userInput = readUserInput(br);
								String folderName = extractFolderName(userInput[1]);
								putFolderName(fileSystem,folderName);
								break;
							}
						case 2: {
								String[] userInput = readUserInput(br);
								Map<String, String> fileEntry = new HashMap<String, String>();
								fileEntry.put(extractFileName(userInput[1]), null);
								
								if(fileSystem.isEmpty() && isLeafFolder(userInput[1])) {
									fileSystem.put(null,fileEntry );
								}else {
									
										fileSystem.put(extractFolderName(userInput[1]), fileEntry);
									
								}
								break;
						}
						case 3: {// write into file
								String[] userInput = readUserInput(br);
								String fileContent = userInput[1];
								String folderStructure = userInput[2];
								String fileName = extractFileName(folderStructure);
								
								Map<String, String> fileEntry = new HashMap<String, String>();
								fileEntry.put(fileName, fileContent);
								
								if(isLeafFolder(folderStructure)) {
									Map<String, String> insideFolder = fileSystem.get(null);
									if(insideFolder == null) { // if user directly selects option 3
										fileSystem.put(null, fileEntry);
									}
								}else {
									String rootFolder = extractFolderName(folderStructure);
									if(fileSystem.get(rootFolder) == null) {
										fileSystem.put(rootFolder, fileEntry);
									}else {
										writeFileContent(fileSystem, fileContent, fileName, rootFolder);
									}
								}
								break;
							}
							
						case 4: { //cat /file1
								String[] userInput = readUserInput(br);
								boolean isLeafFolder = isLeafFolder(userInput[1]);
								String rootFolderName = extractFolderName(userInput[1]);
								Map<String, String> fileList = null;
								
								if(isLeafFolder) {
									 fileList = fileSystem.get(null);
								}else {
									fileList = fileSystem.get(rootFolderName);
								}
								
								if(fileList!= null && !fileList.isEmpty()) {
									for(Map.Entry<String,String> entry : fileList.entrySet()) {
										System.out.println("File name : " + entry.getKey() + " File Content :" + entry.getValue());
									}
								}
								
								break;
							}
						case 5 : { // find file
								String[] userInput = readUserInput(br);
								String fileName = userInput[1];
								
								if(!fileSystem.isEmpty()) {
									for(Map.Entry<String, Map<String, String>> entry : fileSystem.entrySet()) {
										if(entry.getValue()!= null && entry.getValue().get(fileName) != null) {
											System.out.println("Found In Location : " + entry.getKey() + " File Content :" + entry.getValue());
										}
									}
									
								}else {
									System.out.println("File not found and file system is empty ");
								}
								break;
							}
						case 6 : { // ls command
							
							String[] userInput = readUserInput(br);
							String folderName = userInput[1];
							printFolderNamesAndContents(fileSystem,folderName);
							break;
						}
							
						case 9:
							System.out.println("Exiting");
							System.exit(0);
							break;
				}
		}catch(Exception e) {
			System.out.println("Exception : " + e.getMessage());
		}
		}
	}

	public void writeFileContent(Map<String, Map<String, String>> fileSystemMap, String fileContent, String fileName, String rootFolder) {
		Map<String, String> insideFolder = fileSystemMap.get(rootFolder);
		if(insideFolder == null) {
			insideFolder = new HashMap<String, String>();
			fileSystemMap.put(rootFolder, insideFolder);
		}
		String content = insideFolder.get(fileName);
		if(content == null) {
			insideFolder.put(fileName, fileContent);
		}else {
			insideFolder.put(fileName, content + fileContent);
		}
	}

	public void printFolderNamesAndContents(Map<String, Map<String, String>> fileSystemMap, String folderName) {
		if(!fileSystemMap.isEmpty()) {
			for(Map.Entry<String, Map<String, String>> entry : fileSystemMap.entrySet()) {
				if(entry.getKey().equals(folderName)) {
					System.out.print("Root Folder name : " + folderName);
					if(entry.getValue() != null)
					for(Map.Entry<String, String> insideFolderEntry : entry.getValue().entrySet()) {
							System.out.println("File Name : " + insideFolderEntry.getKey() + " File Content :" + insideFolderEntry.getValue());
					}
				}
			}
			
		}else {
			System.out.print("File not found and file system is empty");
		}
	}

	public void putFolderName(Map<String, Map<String, String>> fileSystemMap , String folderName ) {
		if(fileSystemMap.get(folderName)  == null ) {
			fileSystemMap.put(folderName, null);
			System.out.println("####### Folder Successfully Created #######" + folderName);
		}
	}
	
	public int userChoice(Scanner br){
		printMenu();
		return br.nextInt();
	}
	
	
	public String[] readUserInput(Scanner br) throws FileSystemException {
		String input = br.next();
		String[] userInputs =  input.split(" ");
		if(commands.valueOf(userInputs[0]) == null) {
			throw new FileSystemException("Invalid command, please try again !");
		}
		return userInputs;
	}

	public String extractFileName(String input) {
		String fileName = input.substring(input.lastIndexOf("/"));
		return fileName;
	}
	
	public String extractFolderName(String input) {
		String[] folderStructure = input.split("/");
		return folderStructure[1];
	}
	
	public boolean isLeafFolder(String input) {
		String[] folderStructure = input.split("/");
		if(folderStructure.length > 1) return true;
		return false;
	}
	
	public String getRootFolder() {
		Set<String> folders = fileSystem.get(0).keySet();
		return folders.iterator().next();
	}
	
	public class FileSystemException extends Exception {

		private static final long serialVersionUID = 1L;
		
		String exceptionMessage;
		
		FileSystemException(String exceptionMessage) {
			this.exceptionMessage = exceptionMessage;
		}
		
		public String getMessage() {
			return exceptionMessage;
		}
		
	}
}