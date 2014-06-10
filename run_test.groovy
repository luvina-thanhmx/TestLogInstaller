public class TestJobServlet{
	public static void main(String [] args){
		String dirFolderTest = System.getProperty("user.dir");
		File dir = new File(dirFolderTest);
		List<String> listCmd;
		def output = ""
		try {
			def pathToWiperdog = ""
			def caseTest = ""
			args.eachWithIndex {item, index ->
				// get path to wiperdog
				if ((index < (args.size() - 1)) && (item == "-p") && (args[index+1] != null)) {
					pathToWiperdog = args[index+1].replaceAll("\"", "").replaceAll("\'", "").trim()
				}
				// get case want to test
				if ((index < (args.size() - 1)) && (item == "-c") && (args[index+1] != null)) {
					caseTest = args[index+1].replaceAll("\"", "").replaceAll("\'", "").trim()
				}
			}
			
			// check value of -c parameter
			if(caseTest == null || caseTest == "" || !caseTest.contains("Case")) {
				println "Incorrect parameters. Please check again value of -c."
				return
			}
			def jarFile = pathToWiperdog + "target/wiperdog-0.2.5-SNAPSHOT-unix.jar"
			if(!(new File(jarFile)).exists()) {
				// build wiperdog install
				listCmd = new LinkedList<String>();
				listCmd.add("mvn")
				listCmd.add("clean")
				listCmd.add("install")
				println "building wiperdog..."
				output = runProcClosure(listCmd, new File(pathToWiperdog), true)
				if (output.contains("BUILD SUCCESS")){
					println "Build wiperdog successfully!"
				} else {
					println "Build wiperdog failure!"
					return
				}
			}
		
			listCmd = new LinkedList<String>();
			listCmd.add("java")
			listCmd.add("-jar")
			listCmd.add(jarFile)
			listCmd.add("-d")
			listCmd.add(dirFolderTest + "/WiperdogTest")
			listCmd.add("-j")
			if (caseTest != "Case2") {//set jetty port is number
				listCmd.add("123456")	
			} else {//set jetty port is not number
				listCmd.add("error")
			}
			if (caseTest != "Case3") {
				listCmd.add("-m")
				listCmd.add("10.0.0.2")
				listCmd.add("-p")
				listCmd.add("28017")
				listCmd.add("-n")
				listCmd.add("wiperdog")
				listCmd.add("-u")
				listCmd.add("sa")
				listCmd.add("-pw")
				listCmd.add("insight")
				listCmd.add("-mp")
				listCmd.add("test@gmail.com")
				listCmd.add("-s")
				listCmd.add("no")
			}

			// get output when run command
			output = runProcClosure(listCmd, dir, true)
			def logFile = new File(pathToWiperdog + "target/WiperdogInstaller.log")
			def logStr

			// check log writed to file
			if(logFile.exists()) {
				// get string in log
				logStr = logFile.getText()
				// check result
				checkResultData(caseTest, output, logStr)
			} else {
				println "Test failure!"			
			}
		}catch(Exception ex){
			ex.printStackTrace()
		}
	}

	/**
	 * check result when run test finish
	 * @param caseTest case test
	 * @param log data in log file
	 */
	public static void checkResultData(String caseTest, String log) {
		if (caseTest == "Case1") {// data when install wiperdog corresponding to dataset
			if (log.contains("Server Port:123456")) {
					println "Test $caseTest successfully!"		
			} else {
					println "Test $caseTest failure!"	
			}
		} else if (caseTest == "Case2") {// have error when install wiperdog
			if (log.contains("Jetty port must be number: -j")) {
					println "Test $caseTest successfully!"		
			} else {
					println "Test $caseTest failure!"	
			}
		} else if (caseTest == "Case3") {// data when install wiperdog successfully is default
			if (log.contains("Database address:127.0.0.1")) {
					println "Test $caseTest successfully!"		
			} else {
					println "Test $caseTest failure!"	
			}
		}
	}
	
	/**
	 * run command with ProcessBuider
	 * @param listCmd list command
	 * @param dir directory of project
	 * @param waitFor 
	 * @return
	 */
	public static String runProcClosure(listCmd,dir,waitFor){
		def output = [:]
		ProcessBuilder builder = new ProcessBuilder(listCmd);
		builder.redirectErrorStream(true);
		builder.directory(dir);
		Process p = builder.start();
		if(waitFor){
			output['exitVal'] = p.waitFor()
		}
		InputStream procOut  = p.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(procOut))
		def line = null
		StringBuffer stdin = new StringBuffer()
		while((line = br.readLine()) != null){
			stdin.append(line + "\n")
		}
		output["message"] = stdin.toString()
		return output["message"]
	}
}
