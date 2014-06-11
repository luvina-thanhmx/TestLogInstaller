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
			if(caseTest == null || caseTest == "") {
				println "Incorrect parameters. Please check again value of -c."
				return
			}
			// path to jar file
			def jarFile = pathToWiperdog + "target/wiperdog-0.2.5-SNAPSHOT-unix.jar"

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

			// set list test case will be run
			def listCase = []
			if (caseTest == "all" || caseTest == "All" || caseTest == "ALL") {
				listCase = ["Case1", "Case2", "Case3"]
			} else {
				listCase.add(caseTest)
			}

			def logFile1 = new File(pathToWiperdog + "target/WiperdogInstaller.log")
			def logFile2 = new File(pathToWiperdog + "target/WiperdogInstaller.log.1")
			def logStr

			// run test case
			listCase.each{
				caseTest = it
				print "\n"
				println "==========Test $caseTest=========="
				// remove logfile
				if(logFile1.exists()) {
					logFile1.delete()
				}
				// remove logfile
				if(logFile2.exists()) {
					logFile2.delete()
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
				println "Extract wiperdog..."
				output = runProcClosure(listCmd, new File(pathToWiperdog + "target"), true)
				sleep(2000)

				// check log writed to file
				if(logFile1.exists()) {
					// get string in log
					logStr = logFile1.getText()
					if(logFile2.exists()) {
						logStr += logFile2.getText()								
					}
					// check result
					println "Checking log..."
					checkResultData(caseTest, logStr)
					sleep(2000)
				} else {
					println "Test failure!"			
				}
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
			println "**Input: Value of parameters without default (ex: jetty port is 123456)"
			println "**Expected: in the log file will show value of parameters corresponding to set"			
			if (log.contains("Server Port:123456")) {
					println "Test $caseTest successfully!"		
			} else {
					println "Test $caseTest failure!"	
			}
		} else if (caseTest == "Case2") {// have error when install wiperdog
			println "**Input: Value of jetty port to set is not number"
			println "**Expected: in the log file will show the message about error"
			if (log.contains("Jetty port must be number: -j")) {
					println "Test $caseTest successfully!"		
			} else {
					println "Test $caseTest failure!"	
			}
		} else if (caseTest == "Case3") {// data when install wiperdog successfully is default
			println "**Input: Value of parameters is default"
			println "**Expected: in the log file will show value of parameters is default"
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
