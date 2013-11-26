package com.modeln.build.perforce;

public class CommonResult {

	private String _stdOutText;
	private String _stdErrText;
	private int _exitValue;
	
	public void setStdOutText(String text) {
		_stdOutText = text;
	}
	public String getStdOutText() {
		return _stdOutText;
	}
	public void setStdErrText(String text) {
		_stdErrText = text;
	}
	public String getStdErrText() {
		return _stdErrText;
	}
	public void setExitValue(int value) {
		_exitValue = value;
	}
	public int getExitValue() {
		return _exitValue;
	}
	
	public CommonResult() {
		super();
		// TODO Auto-generated constructor stub
	}

}
