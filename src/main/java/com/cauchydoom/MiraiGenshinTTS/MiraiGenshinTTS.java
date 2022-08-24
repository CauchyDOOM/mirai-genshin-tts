 /*
 *                 God Bless No Bugs!
 *
 *                       _ooOoo_
 *                      o8888888o
 *                      88" . "88
 *                      (| -_- |)
 *                      O\  =  /O
 *                   ____/`---'\____
 *                 .'  \\|     |//  `.
 *                /  \\|||  :  |||//  \
 *               /  _||||| -:- |||||-  \
 *               |   | \\\  -  /// |   |
 *               | \_|  ''\---/''  |   |
 *               \  .-\__  `-`  ___/-. /
 *             ___`. .'  /--.--\  `. . __
 *          ."" '<  `.___\_<|>_/___.'  >'"".
 *         | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 *         \  \ `-.   \_ __\ /__ _/   .-` /  /
 *    ======`-.____`-.___\_____/___.-`____.-'======
 *                       `=---='
 *    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
*/
package com.cauchydoom.MiraiGenshinTTS;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

import com.cauchydoom.MiraiGenshinTTS.cardprovider.AmrVoiceProvider;
import com.cauchydoom.MiraiGenshinTTS.cardprovider.MiraiMusicCard;
import com.cauchydoom.MiraiGenshinTTS.cardprovider.SilkVoiceProvider;
import com.cauchydoom.MiraiGenshinTTS.cardprovider.XMLCardProvider;
import com.cauchydoom.MiraiGenshinTTS.musicsource.YaeMikoSource;

import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.event.events.StrangerMessageEvent;
import net.mamoe.mirai.utils.MiraiLogger;
import net.mamoe.yamlkt.Yaml;
import net.mamoe.yamlkt.YamlElement;
import net.mamoe.yamlkt.YamlLiteral;
import net.mamoe.yamlkt.YamlMap;

public class MiraiGenshinTTS extends JavaPlugin {
	public MiraiGenshinTTS() {
		super(new JvmPluginDescriptionBuilder(PluginData.id, PluginData.ver).name(PluginData.name)
				.author(PluginData.auth).info(PluginData.info).build());
	}

	private final String spliter = " ";
	// 请求TTS的线程池。
	private Executor exec = Executors.newFixedThreadPool(8);

	/** 命令列表. */
	public static final Map<String, BiConsumer<MessageEvent, String[]>> commands = new ConcurrentHashMap<>();

	/** TTS来源. */
	public static final Map<String, MusicSource> sources = Collections.synchronizedMap(new LinkedHashMap<>());

	/** 外观来源 */
	public static final Map<String, MusicCardProvider> cards = new ConcurrentHashMap<>();
	static {
		// 注册TTS来源
		sources.put("八重神子", new YaeMikoSource());
		// 注册外观
		cards.put("Silk", new SilkVoiceProvider());
		cards.put("AMR", new AmrVoiceProvider());
		cards.put("Mirai", new MiraiMusicCard());
		cards.put("XML", new XMLCardProvider());
	}
	static {
		HttpURLConnection.setFollowRedirects(true);
	}
	private static MiraiGenshinTTS plugin;

	public static MiraiLogger getMLogger() {
		return plugin.getLogger();
	}

	String unfoundSong;
	String unavailableShare;
	String templateNotFound;
	String sourceNotFound;

	/**
	 * 使用现有的来源和外观制作指令执行器
	 * 
	 * @param source 音乐来源名称
	 * @param card   音乐外观名称
	 * @return return 返回一个指令执行器，可以注册到命令列表里面
	 */
	public BiConsumer<MessageEvent, String[]> makeTemplate(String source, String card) {
		if (source.equals("all"))
			return makeSearchesTemplate(card);
		MusicCardProvider cb = cards.get(card);
		if (cb == null)
			throw new IllegalArgumentException("card template not exists");
		MusicSource mc = sources.get(source);
		if (mc == null)
			throw new IllegalArgumentException("music source not exists");
		return (event, args) -> {
			String sn = String.join(spliter, Arrays.copyOfRange(args, 1, args.length));
			exec.execute(() -> {
				MusicInfo mi;
				try {
					mi = mc.get(sn);
				} catch (Throwable t) {
					this.getLogger().debug(t);
					Utils.getProperReceiver(event).sendMessage(unfoundSong);
					return;
				}
				try {
					Utils.getProperReceiver(event).sendMessage(cb.process(mi, Utils.getProperReceiver(event)));
				} catch (Throwable t) {
					this.getLogger().debug(t);
					// this.getLogger().
					Utils.getProperReceiver(event).sendMessage(unavailableShare);
					return;
				}
			});
		};
	}

	/**
	 * 自动搜索所有源并且以指定外观返回
	 * 
	 * @param card 音乐外观名称
	 * @return return 返回一个指令执行器，可以注册到命令列表里面
	 */
	public BiConsumer<MessageEvent, String[]> makeSearchesTemplate(String card) {
		MusicCardProvider cb = cards.get(card);
		if (cb == null)
			throw new IllegalArgumentException("card template not exists");
		return (event, args) -> {
			String sn = String.join(spliter, Arrays.copyOfRange(args, 1, args.length));

			exec.execute(() -> {
				for (MusicSource mc : sources.values()) {
					if (!mc.isVisible())
						continue;
					MusicInfo mi;
					try {
						mi = mc.get(sn);
					} catch (Throwable t) {
						this.getLogger().debug(t);
						continue;
					}
					try {
						Utils.getProperReceiver(event).sendMessage(cb.process(mi, Utils.getProperReceiver(event)));
					} catch (Throwable t) {
						this.getLogger().debug(t);
						Utils.getProperReceiver(event).sendMessage(unavailableShare);
					}
					return;
				}
				Utils.getProperReceiver(event).sendMessage(unfoundSong);
			});

		};
	}

	@SuppressWarnings("resource")
	@Override
	public void onEnable() {
		plugin = this;
		if (!new File(this.getDataFolder(), "config.yml").exists()) {
			try {
				new FileOutputStream(new File(this.getDataFolder(), "config.yml"))
						.write(Utils.readAll(this.getResourceAsStream("config.yml")));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		reload();
		GlobalEventChannel.INSTANCE.registerListenerHost(new SimpleListenerHost(this.getCoroutineContext()) {
			@EventHandler
			public void onGroup(GroupMessageEvent event) {
				String[] args = Utils.getPlainText(event.getMessage()).split(spliter);
				BiConsumer<MessageEvent, String[]> exec = commands.get(args[0]);
				if (exec != null)
					exec.accept(event, args);

			}

			@EventHandler
			public void onFriend(FriendMessageEvent event) {
				String[] args = Utils.getPlainText(event.getMessage()).split(spliter);
				BiConsumer<MessageEvent, String[]> exec = commands.get(args[0]);
				if (exec != null)
					exec.accept(event, args);

			}

			@EventHandler
			public void onTemp(StrangerMessageEvent event) {
				String[] args = Utils.getPlainText(event.getMessage()).split(spliter);
				BiConsumer<MessageEvent, String[]> exec = commands.get(args[0]);
				if (exec != null)
					exec.accept(event, args);

			}
		});
		getLogger().info("插件加载完毕!");
	}

	public void reload() {
		YamlMap cfg = Yaml.getDefault().decodeYamlMapFromString(
				new String(Utils.readAll(new File(this.getDataFolder(), "config.yml")), StandardCharsets.UTF_8));
		YamlMap excs = (YamlMap) cfg.get(new YamlLiteral("extracommands"));
		String addDefault = cfg.getStringOrNull("adddefault");
		commands.clear();
		if (addDefault == null || addDefault.equals("true")) {
			commands.put("#原神卡片", makeSearchesTemplate("Mirai"));
			commands.put("#原神语音", makeSearchesTemplate("Silk"));
			commands.put("#八重神子卡片", makeTemplate("八重神子", "Mirai"));// 标准样板
			commands.put("#八重神子说", makeTemplate("网易", "Silk"));
		}
		if (excs != null)
			for (YamlElement cmd : excs.getKeys()) {
				commands.put(cmd.toString(), makeTemplate(((YamlMap) excs.get(cmd)).getString("source"),
						((YamlMap) excs.get(cmd)).getString("card")));
			}
		AmrVoiceProvider.ffmpeg = SilkVoiceProvider.ffmpeg = cfg.getString("ffmpeg_path");
		String amras = cfg.getStringOrNull("amrqualityshift");
		String amrwb = cfg.getStringOrNull("amrwb");
		String usecc = cfg.getStringOrNull("use_custom_ffmpeg_command");
		String vb = cfg.getStringOrNull("verbose");
		unfoundSong = cfg.getStringOrNull("hintsongnotfound");
		if (unfoundSong == null)
			unfoundSong = "无法找到歌曲。";
		unavailableShare = cfg.getStringOrNull("hintcarderror");
		if (unavailableShare == null)
			unavailableShare = "分享歌曲失败。";
		templateNotFound = cfg.getStringOrNull("hintnotemplate");
		if (templateNotFound == null)
			templateNotFound = "无法找到卡片。";
		sourceNotFound = cfg.getStringOrNull("hintsourcenotfound");
		if (sourceNotFound == null)
			sourceNotFound = "无法找到来源。";
		AmrVoiceProvider.autoSize = amras != null && amras.equals("true");
		AmrVoiceProvider.wideBrand = amrwb == null || amrwb.equals("true");
		AmrVoiceProvider.customCommand = (usecc != null && usecc.equals("true"))
				? cfg.getStringOrNull("custom_ffmpeg_command")
				: null;
		Utils.verbose = vb == null || vb.equals("true");
		SilkVoiceProvider.silk = cfg.getString("silkenc_path");
		if (AmrVoiceProvider.customCommand == null) {
			try {
				Utils.exeCmd(AmrVoiceProvider.ffmpeg, "-version");
			} catch (RuntimeException ex) {
				ex.printStackTrace();
				getLogger().warning("ffmpeg启动失败，语音功能失效！");
			}
			getLogger().info("当前配置项：宽域AMR:" + AmrVoiceProvider.wideBrand + " AMR自动大小:" + AmrVoiceProvider.autoSize);
		} else
			getLogger().info("当前配置项：自定义指令:" + AmrVoiceProvider.customCommand);
	}
}
