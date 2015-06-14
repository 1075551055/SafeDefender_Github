package com.water.safedefender.domian;

import android.graphics.drawable.Drawable;

public class TaskInfo {

	private Drawable Icon;
	private String name;
	private String packagename;
	private long memerySize;
	private boolean checked;
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	@Override
	public String toString() {
		return "TaskInfo [Icon=" + Icon + ", name=" + name + ", packagename="
				+ packagename + ", memerySize=" + memerySize + ", userTask="
				+ userTask + "]";
	}
	private boolean userTask;
	public Drawable getIcon() {
		return Icon;
	}
	public void setIcon(Drawable icon) {
		Icon = icon;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPackagename() {
		return packagename;
	}
	public void setPackagename(String packagename) {
		this.packagename = packagename;
	}
	public long getMemerySize() {
		return memerySize;
	}
	public void setMemerySize(long memerySize) {
		this.memerySize = memerySize;
	}
	public boolean isUserTask() {
		return userTask;
	}
	public void setUserTask(boolean userTask) {
		this.userTask = userTask;
	}
}
