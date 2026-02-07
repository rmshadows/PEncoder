package appCtrl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * 使用 UTF-8 加载 .properties，以便资源文件内直接写中文。
 */
public class UTF8Control extends ResourceBundle.Control {
	@Override
	public ResourceBundle newBundle(String baseName, Locale locale, String format,
			ClassLoader loader, boolean reload) throws IOException, IllegalAccessException, InstantiationException {
		String bundleName = toBundleName(baseName, locale);
		String resourceName = toResourceName(bundleName, "properties");
		InputStream stream = null;
		if (reload) {
			URL url = loader.getResource(resourceName);
			if (url != null) {
				URLConnection conn = url.openConnection();
				if (conn != null) {
					conn.setUseCaches(false);
					stream = conn.getInputStream();
				}
			}
		} else {
			stream = loader.getResourceAsStream(resourceName);
		}
		if (stream != null) {
			try {
				return new PropertyResourceBundle(new InputStreamReader(stream, StandardCharsets.UTF_8));
			} finally {
				stream.close();
			}
		}
		return super.newBundle(baseName, locale, format, loader, reload);
	}
}
