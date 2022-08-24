package com.cauchydoom.MiraiGenshinTTS.cardprovider;

import com.cauchydoom.MiraiGenshinTTS.MusicCardProvider;
import com.cauchydoom.MiraiGenshinTTS.MusicInfo;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.SimpleServiceMessage;

public class XMLCardProvider implements MusicCardProvider {

	public XMLCardProvider() {
	}
	
	@Override
	public MessageChain process(MusicInfo mi, Contact ct) {
		StringBuilder xmb = new StringBuilder("<msg serviceID=\"2\" templateID=\"1\" action=\"web\" actionData=\"\" a_actionData=\"\" i_actionData=\"\" brief=\"[音乐]")
				.append(escapeXmlTag(mi.title)).append("\" sourceMsgId=\"0\" url=\"").append(escapeXmlTag(mi.jurl))
				.append("\" flag=\"0\" adverSign=\"0\" multiMsgFlag=\"0\">\r\n<item layout=\"2\">\r\n")
				.append("<audio cover=\"").append(escapeXmlTag(mi.purl)).append("\" src=\"")
				.append(escapeXmlTag(mi.murl)).append("\"/>\r\n").append("<title>").append(escapeXmlContent(mi.title))
				.append("</title>\r\n<summary>").append(escapeXmlContent(mi.desc))
				.append("</summary>\r\n</item>\r\n<source name=\"").append(escapeXmlTag(mi.source)).append("\" icon=\"")
				.append(escapeXmlTag(mi.icon))
				.append("\" url=\"\" action=\"web\" a_actionData=\"tencent0://\" i_actionData=\"\" appid=\"").append(mi.appid)
				.append("\"/>\r\n</msg>");
		Message msg = new SimpleServiceMessage(2, xmb.toString());
		return msg.plus(mi.jurl);
	}

	public String escapeXmlContent(String org) {
		return org.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}

	public String escapeXmlTag(String org) {
		return org.replaceAll("\\&", "&amp;").replaceAll("\"", "&quot;").replaceAll("'", "&apos;")
				.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}
}
