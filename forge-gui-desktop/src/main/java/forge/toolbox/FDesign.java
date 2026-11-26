/*
 * Forge: Play Magic: the Gathering.
 * Copyright (C) 2011  Forge Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package forge.toolbox;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;

/**
 * Arcane Glass: the desktop client's design-token layer.
 *
 * <p>The old desktop skin encoded colours in individual pixels of a sprite and
 * assembled controls from glossy image strips. That made the appearance hard
 * to reason about and left a patchwork of hard-coded gradients throughout the
 * views. This class keeps one palette, one typography scale, and a handful of
 * reusable paint operations. {@link FSkin} still owns user-installed skins;
 * the default skin now resolves its legacy colour tokens through this class.</p>
 */
public final class FDesign {
    /** A platform-neutral UI face; card text continues to use Forge's own fonts. */
    public static final String FONT_FAMILY = "SansSerif";

    public static final int RADIUS = 12;
    public static final int CONTROL_HEIGHT = 40;
    public static final int SPACING = 8;
    public static final int SIDEBAR_WIDTH = 248;

    public static final Color BASE = color(0x0A0F18);
    public static final Color SIDEBAR = color(0x0D1522);
    public static final Color SURFACE = color(0x121C2B);
    public static final Color SURFACE_RAISED = color(0x18263A);
    public static final Color SURFACE_SOFT = color(0x1D3048);
    public static final Color HOVER = color(0x21334A);
    public static final Color SELECTED_SOFT = color(0x1B4260);
    public static final Color BORDER = color(0x2A3C54);
    public static final Color BORDER_STRONG = color(0x3D5673);

    public static final Color TEXT = color(0xF2F6FC);
    public static final Color TEXT_MUTED = color(0xA9B8CC);
    public static final Color TEXT_SUBTLE = color(0x6F8299);
    public static final Color TEXT_ON_ACCENT = color(0x07131F);

    public static final Color ACCENT = color(0x7DD3FC);
    public static final Color ACCENT_HOVER = color(0xA5E8FF);
    public static final Color ACCENT_PRESSED = color(0x38BDF8);
    public static final Color ACCENT_MUTED = color(0x256D8D);
    public static final Color FOCUS = color(0xC4B5FD);
    public static final Color VIOLET = color(0x8B5CF6);
    public static final Color SUCCESS = color(0x34D399);
    public static final Color WARNING = color(0xFBBF24);
    public static final Color DANGER = color(0xF87171);
    public static final Color OVERLAY = new Color(4, 8, 15, 190);
    public static final Color DISABLED = color(0x2B3545);

    private FDesign() {
    }

    /** @return a font from the design system's platform-neutral family. */
    public static Font font(final int size, final boolean bold) {
        return new Font(FONT_FAMILY, bold ? Font.BOLD : Font.PLAIN,
                Math.max(1, FSkin.getRelativeFontSize(size)));
    }

    /** @return a copy of {@code color} with the supplied 0-255 alpha. */
    public static Color withAlpha(final Color color, final int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), clamp(alpha, 0, 255));
    }

    /** @return a linear blend of two colours, {@code amount} clamped to 0..1. */
    public static Color mix(final Color first, final Color second, final float amount) {
        final float t = Math.max(0f, Math.min(1f, amount));
        final int r = Math.round(first.getRed() * (1f - t) + second.getRed() * t);
        final int g = Math.round(first.getGreen() * (1f - t) + second.getGreen() * t);
        final int b = Math.round(first.getBlue() * (1f - t) + second.getBlue() * t);
        return new Color(r, g, b);
    }

    /** @return true for the built-in theme; user-installed skins keep their own swatches. */
    public static boolean isBuiltInTheme(final String skinName) {
        return skinName == null || skinName.isEmpty() || "default".equalsIgnoreCase(skinName);
    }

    /** Maps Forge's legacy skin tokens onto the modern default palette. */
    public static Color colorFor(final FSkin.Colors token) {
        if (token == null) {
            return TEXT;
        }
        switch (token) {
            case CLR_THEME:
                return SURFACE;
            case CLR_THEME2:
                return BASE;
            case CLR_BORDERS:
                return BORDER;
            case CLR_ZEBRA:
                return SURFACE_RAISED;
            case CLR_HOVER:
                return HOVER;
            case CLR_ACTIVE:
                // Legacy widgets often render white text over the active colour.
                return ACCENT_MUTED;
            case CLR_INACTIVE:
                return SURFACE_RAISED;
            case CLR_TEXT:
                return TEXT;
            case CLR_PHASE_INACTIVE_ENABLED:
                return TEXT_MUTED;
            case CLR_PHASE_INACTIVE_DISABLED:
                return TEXT_SUBTLE;
            case CLR_PHASE_ACTIVE_ENABLED:
                return WARNING;
            case CLR_PHASE_ACTIVE_DISABLED:
                return color(0x8A6A22);
            case CLR_OVERLAY:
                return OVERLAY;
            case CLR_COMBAT_TARGETING_ARROW:
                return DANGER;
            case CLR_NORMAL_TARGETING_ARROW:
                return ACCENT;
            case CLR_PWATTK_TARGETING_ARROW:
                return FOCUS;
            default:
                return TEXT;
        }
    }

    /** Paints a flat, accessible button face. Text is left to the Swing button. */
    public static void drawButton(final Graphics2D g, final int width, final int height,
            final boolean hovered, final boolean pressed, final boolean toggled,
            final boolean focused, final boolean enabled) {
        if (width <= 0 || height <= 0) {
            return;
        }
        final Graphics2D copy = (Graphics2D) g.create();
        try {
            copy.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            copy.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            final int radius = Math.min(height / 2, RADIUS);
            final Color fill;
            if (!enabled) {
                fill = DISABLED;
            } else if (pressed || toggled) {
                fill = ACCENT_PRESSED;
            } else if (hovered) {
                fill = ACCENT_HOVER;
            } else {
                fill = ACCENT;
            }

            copy.setColor(withAlpha(Color.BLACK, enabled ? 55 : 20));
            copy.fillRoundRect(1, 2, width - 2, height - 2, radius, radius);
            copy.setPaint(new GradientPaint(0, 0, mix(fill, Color.WHITE, 0.06f),
                    0, height, mix(fill, Color.BLACK, 0.05f)));
            copy.fillRoundRect(0, 0, width - 1, height - 2, radius, radius);
            copy.setColor(enabled ? withAlpha(TEXT_ON_ACCENT, 70) : withAlpha(TEXT_MUTED, 45));
            copy.drawRoundRect(0, 0, width - 1, height - 2, radius, radius);

            if (focused && enabled) {
                copy.setColor(FOCUS);
                copy.setStroke(new BasicStroke(2f));
                copy.drawRoundRect(2, 2, width - 5, height - 6, radius, radius);
            }
        } finally {
            copy.dispose();
        }
    }

    /** Paints a card-like surface with a restrained hairline border. */
    public static void drawSurface(final Graphics2D g, final int width, final int height) {
        final Graphics2D copy = (Graphics2D) g.create();
        try {
            copy.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            copy.setColor(SURFACE);
            copy.fillRoundRect(0, 0, width, height, RADIUS, RADIUS);
            copy.setColor(BORDER);
            copy.drawRoundRect(0, 0, width - 1, height - 1, RADIUS, RADIUS);
        } finally {
            copy.dispose();
        }
    }

    /** Paints sidebar selection using a solid wash and a compact accent rail. */
    public static void drawSidebarItem(final Graphics2D g, final int width, final int height,
            final boolean selected, final boolean hovered) {
        if (selected) {
            g.setColor(SELECTED_SOFT);
            g.fillRect(0, 0, width, height);
            g.setColor(ACCENT);
            g.fillRect(0, 3, 3, Math.max(1, height - 6));
        } else if (hovered) {
            g.setColor(HOVER);
            g.fillRect(0, 0, width, height);
        }
        g.setColor(BORDER);
        g.drawLine(0, height - 1, width, height - 1);
    }

    /** Paints a compact navigation tab with an accent underline when selected. */
    public static void drawNavTab(final Graphics2D g, final int width, final int height,
            final boolean selected, final boolean hovered) {
        final Graphics2D copy = (Graphics2D) g.create();
        try {
            copy.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            copy.setColor(selected ? SURFACE_RAISED : hovered ? HOVER : SIDEBAR);
            copy.fillRoundRect(0, 0, width, height, 8, 8);
            copy.setColor(selected ? ACCENT : BORDER);
            copy.drawRoundRect(0, 0, width - 1, height - 1, 8, 8);
            if (selected) {
                copy.setColor(ACCENT);
                copy.fillRect(6, height - 2, Math.max(0, width - 12), 2);
            }
        } finally {
            copy.dispose();
        }
    }

    /** Paints a slim rounded scrollbar thumb. */
    public static void drawScrollThumb(final Graphics2D g, final int x, final int y,
            final int width, final int height, final boolean hovered) {
        final Graphics2D copy = (Graphics2D) g.create();
        try {
            copy.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            copy.setColor(hovered ? ACCENT_MUTED : BORDER_STRONG);
            copy.fillRoundRect(x, y, width, height, Math.min(width, height), Math.min(width, height));
        } finally {
            copy.dispose();
        }
    }

    /** @return the standard border for inputs and compact controls. */
    public static Border controlBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10));
    }

    /** @return the standard border around a raised panel. */
    public static Border surfaceBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14));
    }

    /**
     * Applies the design system to Swing defaults. This runs after the Metal
     * look-and-feel is installed so every plain Swing component created by a
     * screen inherits the same palette and typography.
     */
    public static void installUIDefaults() {
        final UIDefaults defaults = UIManager.getDefaults();
        final Border inputBorder = controlBorder();
        final Border surface = surfaceBorder();

        defaults.put("Panel.background", ui(SURFACE));
        defaults.put("Panel.foreground", ui(TEXT));
        defaults.put("OptionPane.background", ui(SURFACE));
        defaults.put("OptionPane.messageForeground", ui(TEXT));
        defaults.put("OptionPane.border", surface);

        defaults.put("Button.background", ui(ACCENT));
        defaults.put("Button.foreground", ui(TEXT_ON_ACCENT));
        defaults.put("Button.select", ui(ACCENT_PRESSED));
        defaults.put("Button.focus", ui(FOCUS));
        defaults.put("Button.border", controlBorder());
        defaults.put("Button.rollover", true);

        defaults.put("CheckBox.background", ui(SURFACE));
        defaults.put("CheckBox.foreground", ui(TEXT));
        defaults.put("CheckBox.focus", ui(FOCUS));
        defaults.put("RadioButton.background", ui(SURFACE));
        defaults.put("RadioButton.foreground", ui(TEXT));
        defaults.put("RadioButton.focus", ui(FOCUS));

        defaults.put("ComboBox.background", ui(SURFACE_RAISED));
        defaults.put("ComboBox.foreground", ui(TEXT));
        defaults.put("ComboBox.selectionBackground", ui(ACCENT));
        defaults.put("ComboBox.selectionForeground", ui(TEXT_ON_ACCENT));
        defaults.put("ComboBox.disabledBackground", ui(DISABLED));
        defaults.put("ComboBox.disabledForeground", ui(TEXT_SUBTLE));
        defaults.put("ComboBox.buttonBackground", ui(SURFACE_RAISED));
        defaults.put("ComboBox.border", inputBorder);

        defaults.put("TextField.background", ui(SURFACE_RAISED));
        defaults.put("TextField.foreground", ui(TEXT));
        defaults.put("TextField.caretForeground", ui(ACCENT));
        defaults.put("TextField.selectionBackground", ui(ACCENT_MUTED));
        defaults.put("TextField.selectionForeground", ui(TEXT));
        defaults.put("TextField.border", inputBorder);
        defaults.put("FormattedTextField.background", ui(SURFACE_RAISED));
        defaults.put("FormattedTextField.foreground", ui(TEXT));
        defaults.put("FormattedTextField.border", inputBorder);
        defaults.put("PasswordField.background", ui(SURFACE_RAISED));
        defaults.put("PasswordField.foreground", ui(TEXT));
        defaults.put("PasswordField.border", inputBorder);
        defaults.put("TextArea.background", ui(SURFACE_RAISED));
        defaults.put("TextArea.foreground", ui(TEXT));
        defaults.put("TextArea.caretForeground", ui(ACCENT));
        defaults.put("TextArea.selectionBackground", ui(ACCENT_MUTED));
        defaults.put("TextArea.selectionForeground", ui(TEXT));
        defaults.put("TextPane.background", ui(SURFACE_RAISED));
        defaults.put("TextPane.foreground", ui(TEXT));
        defaults.put("EditorPane.background", ui(SURFACE_RAISED));
        defaults.put("EditorPane.foreground", ui(TEXT));

        defaults.put("List.background", ui(SURFACE_RAISED));
        defaults.put("List.foreground", ui(TEXT));
        defaults.put("List.selectionBackground", ui(ACCENT_MUTED));
        defaults.put("List.selectionForeground", ui(TEXT));
        defaults.put("Table.background", ui(SURFACE_RAISED));
        defaults.put("Table.foreground", ui(TEXT));
        defaults.put("Table.gridColor", ui(BORDER));
        defaults.put("Table.selectionBackground", ui(ACCENT_MUTED));
        defaults.put("Table.selectionForeground", ui(TEXT));
        defaults.put("TableHeader.background", ui(SURFACE));
        defaults.put("TableHeader.foreground", ui(TEXT_MUTED));
        defaults.put("Tree.background", ui(SURFACE_RAISED));
        defaults.put("Tree.foreground", ui(TEXT));
        defaults.put("Tree.selectionBackground", ui(ACCENT_MUTED));
        defaults.put("Tree.selectionForeground", ui(TEXT));
        defaults.put("Tree.line", ui(BORDER));

        defaults.put("ScrollPane.background", ui(SURFACE));
        defaults.put("ScrollPane.foreground", ui(TEXT));
        defaults.put("ScrollPane.border", BorderFactory.createLineBorder(BORDER));
        defaults.put("Viewport.background", ui(SURFACE));
        defaults.put("Viewport.foreground", ui(TEXT));
        defaults.put("ScrollBar.background", ui(SURFACE));
        defaults.put("ScrollBar.foreground", ui(TEXT_MUTED));
        defaults.put("ScrollBar.track", ui(SURFACE));
        defaults.put("ScrollBar.thumb", ui(BORDER_STRONG));
        defaults.put("ScrollBar.thumbDarkShadow", ui(BORDER_STRONG));
        defaults.put("ScrollBar.thumbHighlight", ui(ACCENT_MUTED));
        defaults.put("ScrollBar.thumbShadow", ui(BORDER));
        defaults.put("ScrollBar.width", 10);
        defaults.put("SplitPane.background", ui(SURFACE));
        defaults.put("SplitPane.dividerSize", 6);

        defaults.put("TabbedPane.background", ui(SIDEBAR));
        defaults.put("TabbedPane.foreground", ui(TEXT_MUTED));
        defaults.put("TabbedPane.selected", ui(SURFACE_RAISED));
        defaults.put("TabbedPane.contentOpaque", false);
        defaults.put("TabbedPane.unselectedBackground", ui(SIDEBAR));
        defaults.put("TabbedPane.focus", ui(FOCUS));

        defaults.put("ProgressBar.background", ui(SURFACE_RAISED));
        defaults.put("ProgressBar.foreground", ui(ACCENT));
        defaults.put("ProgressBar.selectionBackground", ui(SURFACE_RAISED));
        defaults.put("ProgressBar.selectionForeground", ui(TEXT));
        defaults.put("ProgressBar.border", BorderFactory.createEmptyBorder());
        defaults.put("ToolTip.background", ui(SURFACE_SOFT));
        defaults.put("ToolTip.foreground", ui(TEXT));
        defaults.put("ToolTip.border", BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_STRONG),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));

        defaults.put("MenuBar.background", ui(SIDEBAR));
        defaults.put("MenuBar.foreground", ui(TEXT));
        defaults.put("MenuBar.border", BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        defaults.put("Menu.background", ui(SIDEBAR));
        defaults.put("Menu.foreground", ui(TEXT));
        defaults.put("Menu.selectionBackground", ui(ACCENT_MUTED));
        defaults.put("Menu.selectionForeground", ui(TEXT));
        defaults.put("Menu.borderPainted", false);
        defaults.put("MenuItem.background", ui(SIDEBAR));
        defaults.put("MenuItem.foreground", ui(TEXT));
        defaults.put("MenuItem.selectionBackground", ui(ACCENT_MUTED));
        defaults.put("MenuItem.selectionForeground", ui(TEXT));
        defaults.put("MenuItem.acceleratorForeground", ui(TEXT_MUTED));
        defaults.put("CheckBoxMenuItem.background", ui(SIDEBAR));
        defaults.put("CheckBoxMenuItem.foreground", ui(TEXT));
        defaults.put("CheckBoxMenuItem.selectionBackground", ui(ACCENT_MUTED));
        defaults.put("CheckBoxMenuItem.selectionForeground", ui(TEXT));
        defaults.put("RadioButtonMenuItem.background", ui(SIDEBAR));
        defaults.put("RadioButtonMenuItem.foreground", ui(TEXT));
        defaults.put("RadioButtonMenuItem.selectionBackground", ui(ACCENT_MUTED));
        defaults.put("RadioButtonMenuItem.selectionForeground", ui(TEXT));
        defaults.put("PopupMenu.background", ui(SIDEBAR));
        defaults.put("PopupMenu.foreground", ui(TEXT));
        defaults.put("PopupMenu.border", BorderFactory.createLineBorder(BORDER));
        defaults.put("Separator.background", ui(SIDEBAR));
        defaults.put("Separator.foreground", ui(BORDER));

        modernizeFonts(defaults);
    }

    private static void modernizeFonts(final UIDefaults defaults) {
        final List<Object> fontKeys = new ArrayList<>();
        final Enumeration<Object> keys = defaults.keys();
        while (keys.hasMoreElements()) {
            final Object key = keys.nextElement();
            if (key.toString().endsWith("font") && defaults.get(key) instanceof FontUIResource) {
                fontKeys.add(key);
            }
        }
        for (final Object key : fontKeys) {
            final FontUIResource old = (FontUIResource) defaults.get(key);
            final int style = old.isBold() ? Font.BOLD : old.isItalic() ? Font.ITALIC : Font.PLAIN;
            final int size = Math.max(12, old.getSize());
            defaults.put(key, new FontUIResource(FONT_FAMILY, style, size));
        }
    }

    private static ColorUIResource ui(final Color color) {
        return new ColorUIResource(color);
    }

    private static Color color(final int rgb) {
        return new Color(rgb);
    }

    private static int clamp(final int value, final int min, final int max) {
        return Math.max(min, Math.min(max, value));
    }
}
