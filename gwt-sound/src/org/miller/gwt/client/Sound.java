package org.miller.gwt.client;

import org.miller.gwt.client.sound.Callback;
import org.miller.gwt.client.sound.ID3;
import org.miller.gwt.client.sound.Option;
import org.miller.gwt.client.sound.SMSound;
import org.miller.gwt.client.sound.SoundManager;
import org.miller.gwt.client.sound.SoundOptions;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Sound implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final String SOUND_ID = "soundID";
		final Button play = new Button("Play");
		final Button pause = new Button("Pause");
		final Button resume = new Button("Resume");
		final Button stop = new Button("Stop");
		final Label duration = new Label("0/0");
		final HorizontalPanel player = new HorizontalPanel();
		final SoundManager sm = SoundManager.getInstance();
		final FlexTable info = new FlexTable();

		final Timer timer = new Timer() {
			public void run() {
				SMSound sound = sm.getSoundById(SOUND_ID);
				duration.setText("" + sound.getPosition() + "/"
						+ sound.getDuration());
			}
		};

		sm.getDefaultOptions().onID3(new Callback() {
			public void execute() {
				SMSound sound = sm.getSoundById(SOUND_ID);
				ID3 id3 = sound.getID3();
				info.setWidget(0, 0, new Label("Comment"));
				info.setWidget(1, 0, new Label("Album"));
				info.setWidget(2, 0, new Label("Genre"));
				info.setWidget(3, 0, new Label("Song Name"));
				info.setWidget(4, 0, new Label("Artist"));
				info.setWidget(5, 0, new Label("Track"));
				info.setWidget(6, 0, new Label("Year"));

				info.setText(0, 1, id3.getV1().getComment());
				info.setText(1, 1, id3.getV1().getAlbum());
				info.setText(2, 1, id3.getV1().getGenre());
				info.setText(3, 1, id3.getV1().getSongName());
				info.setText(4, 1, id3.getV1().getArtist());
				info.setText(5, 1, id3.getV1().getTrack());
				info.setText(6, 1, id3.getV1().getYear());
			}
		});

		sm.getDefaultOptions().onPlay(new Callback() {
			public void execute() {
				timer.scheduleRepeating(1000);
			}
		});

		play.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				duration.setText("0/0");
				/**
				 * 1.) sm.play(SOUND_ID, "sound/Mist.mp3");
				 * 
				 * 2.) sm.createSound(SOUND_ID, "sound/Mist.mp3");
				 * sm.play(SOUND_ID);
				 * 
				 * 3.) sm.createSound(new Option[] { SoundOptions.id(SOUND_ID),
				 * SoundOptions.url("sound/Mist.mp3"), SoundOptions.onLoad(new
				 * Callback() { public void execute() { Window.alert("loaded"); } })
				 * }); sm.play(SOUND_ID);
				 * 
				 * 4.) sm.loadFromXML(XML); sm.play(SOUND_ID);
				 */

				final String XML = "<items baseHref=\"sound/\">"
						+ "<sound id=\"Mist\" href=\"Mist.mp3\"/>"
						+ "<sound id=\"Hello\" href=\"Hello.mp3\"/>"
						+ "</items>";
				sm.loadFromXML(XML);
				sm.play("Mist", new Option[] {
						SoundOptions.onFinish(new Callback() {
							public void execute() {
								sm.play("Hello");
							}
						})
				});
			}
		});

		pause.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				sm.pause(SOUND_ID);
			}
		});

		resume.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				sm.resume(SOUND_ID);
			}
		});

		stop.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				sm.stop(SOUND_ID);
				sm.destroySound(SOUND_ID);
				timer.cancel();
				duration.setText("0/0");
			}
		});

		sm.onLoad(new Callback() {
			public void execute() {
				duration.setText("Ready...");
			}
		});

		player.add(play);
		player.add(pause);
		player.add(resume);
		player.add(stop);
		player.add(duration);

		RootPanel.get().add(player);
		RootPanel.get().add(info);
	}
}
