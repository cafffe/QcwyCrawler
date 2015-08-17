package com.kmgh;

public class LoginResultObj {
	private String postresult;
	private String jobsnum;
	private String resumesnum;
	private boolean result;
	
	public boolean getResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public String getPostresult() {
		return postresult;
	}
	public void setPostresult(String postresult) {
		this.postresult = postresult;
	}
	public String getJobsnum() {
		return jobsnum;
	}
	public void setJobsnum(String jobsnum) {
		this.jobsnum = jobsnum;
	}
	public String getResumesnum() {
		return resumesnum;
	}
	public void setResumesnum(String resumesnum) {
		this.resumesnum = resumesnum;
	}
}
