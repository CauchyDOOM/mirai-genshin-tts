package com.cauchydoom.MiraiGenshinTTS.musicsource;

import com.cauchydoom.MiraiGenshinTTS.MusicInfo;
import com.cauchydoom.MiraiGenshinTTS.MusicSource;
import com.cauchydoom.MiraiGenshinTTS.Utils;

public class VentiSource implements MusicSource {

	@Override
	public MusicInfo get(String keyword) throws Exception {
		keyword=Utils.urlEncode(keyword);
		return new MusicInfo("GenshinTTS", "Venti", "https://i2.hdslb.com/bfs/face/d2a95376140fb1e5efbcbed70ef62891a3e5284f.jpg", "http://233366.proxy.nscc-gz.cn:8888/?speaker=" + Utils.urlEncode("温迪") + "&text=" + keyword, "http://233366.proxy.nscc-gz.cn:8888/?speaker=" + Utils.urlEncode("温迪") + "&text=" + keyword, "原神", "", 100495085);
	}

}