package akqa;

public enum SourceFile {

	
	INPUTFILE("input.txt"),
	OUTPUTFILE("output.txt");
	
	String fileName;
	
	private SourceFile(String file) {
		this.fileName = file;
	}
	
	public String getSourceFile() {
		return this.fileName;
	}
	
}
