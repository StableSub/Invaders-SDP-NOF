package engine.manager;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.Logger;

import engine.utility.Score;
import engine.core.Core;
import engine.manager.DrawManager.SpriteType;
import entity.Wallet;

/**
 * Manages files used in the application.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public final class FileManager {

	/**
	 * Singleton instance of the class.
	 */
	private static FileManager instance;
	/**
	 * Application logger.
	 */
	private static Logger logger;

	/**
	 * private constructor.
	 */
	private FileManager() {
		logger = Core.getLogger();
	}

	/**
	 * Returns shared instance of FileManager.
	 *
	 * @return Shared instance of FileManager.
	 */
	public static FileManager getInstance() {
		if (instance == null)
			instance = new FileManager();
		return instance;
	}

	/**
	 * Loads sprites from disk.
	 *
	 * @param spriteMap Mapping of sprite type and empty boolean matrix that will
	 *                  contain the image.
	 * @throws IOException In case of loading problems.
	 */
	public void loadSprite(final Map<SpriteType, boolean[][]> spriteMap) throws IOException {
		try (InputStream inputStream = new FileInputStream("res/graphics");
			 BufferedReader reader = inputStream != null ? new BufferedReader(new InputStreamReader(inputStream)) : null) {

			if (reader == null)
				throw new IOException("Graphics file not found.");

			String line;

			// Sprite loading.
			for (Map.Entry<SpriteType, boolean[][]> sprite : spriteMap.entrySet()) {

				int idx = 0;
				do {
					line = reader.readLine();

					if (line == null)
						throw new IOException("Sprite data not found.");

				} while (line.trim().isEmpty() || line.trim().startsWith("#"));

				for (int i = 0; i < sprite.getValue().length; i++) {
					for (int j = 0; j < sprite.getValue()[i].length; j++) {
						char c = line.charAt(idx++);
						sprite.getValue()[i][j] = c == '1';
					}
				}

				logger.fine("Sprite " + sprite.getKey() + " loaded.");
			}
		}
	}

	/**
	 * Loads a font of a given size.
	 *
	 * @param size Point size of the font.
	 * @return New font.
	 * @throws IOException         In case of loading problems.
	 * @throws FontFormatException In case of incorrect font format.
	 */
	public Font loadFont(final float size) throws IOException, FontFormatException {
		InputStream inputStream = null;
		Font font = null;

		try {
			// Load font from resource
			inputStream = new FileInputStream("res/space_invaders.ttf");
			if (inputStream == null) {
				throw new FileNotFoundException("Font file not found in classpath.");
			}

			font = Font.createFont(Font.TRUETYPE_FONT, inputStream).deriveFont(size);
		} catch (FontFormatException e) {
			System.err.println("Invalid font format: " + e.getMessage());
			throw e;
		} catch (IOException e) {
			System.err.println("Error loading font file: " + e.getMessage());
			throw e;
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}

		// Default font fallback
		if (font == null) {
			System.err.println("Font loading failed. Using default font.");
			font = new Font("SansSerif", Font.PLAIN, (int) size);
		}

		return font;
	}

	public List<String> loadCreditList() throws IOException {  // 사용자의 크레딧 파일을 로드

		List<String> creditname = new ArrayList<String>();
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;

		try {
			inputStream = FileManager.class.getClassLoader()
					.getResourceAsStream("creditlist");
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

			logger.info("Loading credit list.");

			String name = bufferedReader.readLine();

			while (name != null) {
				creditname.add(name);
				name = bufferedReader.readLine();
			}

		} finally {
			if (bufferedReader != null)
				bufferedReader.close();
		}

		return creditname;
	}
}


