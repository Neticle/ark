package pt.neticle.ark.view.arktemplating;

import pt.neticle.ark.config.Setting;
import pt.neticle.ark.config.SettingsBundle;
import pt.neticle.ark.filesystem.ArkFs;

import java.nio.file.Path;

public class ArkTemplatingSettings extends SettingsBundle
{
    public static final Setting<Path> templatesBasePath = new Setting<>(() -> ArkFs.resolveBundled("templates"));

    public static final Setting<Boolean> hotReload = new Setting<>(() -> false);
}
