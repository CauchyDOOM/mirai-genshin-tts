package com.cauchydoom.MiraiGenshinTTS;

import java.util.Map;

public class MusicInfo {
	
	/** The title.<br>音乐名称 */
	public final String title;
	
	/** The desc.<br>音乐描述 */
	public final String desc;
	
	/** The purl.<br>图片地址，必须能够外界直接访问 */
	public final String purl;
	
	/** The murl.<br>音频地址，必须能够外界直接访问 */
	public final String murl;
	
	/** The jurl.<br>跳转地址，必须能够外界直接访问 */
	public final String jurl;
	
	/** The source.<br>音乐源名称 */
	public final String source;
	
	/** The icon.<br>音乐源图标地址 */
	public final String icon;
	
	/** The appid.<br>音乐软件appid，用于发送卡片 */
	public final long appid;
	
	/** The properties.<br>请求上述链接的而外http HEAD，可能为null */
	public Map<String, String> properties;

	/**
	 * Instantiates a new MusicInfo.<br>
	 *
	 * @param title the title<br>
	 * @param desc the desc<br>
	 * @param purl the purl<br>
	 * @param murl the murl<br>
	 * @param jurl the jurl<br>
	 * @param source the source<br>
	 * @param icon the icon<br>
	 * @param appid the appid<br>
	 */
	public MusicInfo(String title, String desc, String purl, String murl, String jurl, String source, String icon,
			long appid) {
		this.title = title;
		this.desc = desc;
		this.purl = purl;
		this.murl = murl;
		this.jurl = jurl;
		this.source = source;
		this.icon = icon;
		this.appid = appid;
	}

	/**
	 * Instantiates a new MusicInfo.<br>
	 *
	 * @param title the title<br>
	 * @param desc the desc<br>
	 * @param purl the purl<br>
	 * @param murl the murl<br>
	 * @param jurl the jurl<br>
	 * @param source the source<br>
	 */
	public MusicInfo(String title, String desc, String purl, String murl, String jurl, String source) {
		this.appid = 0;
		this.icon = "";
		this.source = source;
		this.title = title;
		this.desc = desc;
		this.purl = purl;
		this.murl = murl;
		this.jurl = jurl;
	}

	/**
	 * Instantiates a new MusicInfo.<br>
	 *
	 * @param title the title<br>
	 * @param desc the desc<br>
	 * @param purl the purl<br>
	 * @param murl the murl<br>
	 * @param jurl the jurl<br>
	 * @param source the source<br>
	 * @param icon the icon<br>
	 */
	public MusicInfo(String title, String desc, String purl, String murl, String jurl, String source, String icon) {
		this.title = title;
		this.desc = desc;
		this.purl = purl;
		this.murl = murl;
		this.jurl = jurl;
		this.source = source;
		this.icon = icon;
		this.appid = 0;
	}
}