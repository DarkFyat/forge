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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;

import forge.gui.UiCommand;
import forge.gui.framework.ILocalRepaint;
import forge.gui.interfaces.IButton;
import forge.localinstance.skin.FSkinProp;
import forge.toolbox.FSkin.Colors;
import forge.toolbox.FSkin.SkinnedButton;
import forge.toolbox.FSkin.SkinColor;

/**
 * The core JButton used throughout the desktop client.
 *
 * <p>The old implementation stitched together six states from bitmap strips.
 * Those assets are retained for user-installed mobile themes, but desktop
 * buttons are now painted from {@link FDesign}: crisp at every DPI, consistent
 * with native controls, and responsive to hover, press, focus and toggle.</p>
 */
@SuppressWarnings("serial")
public class FButton extends SkinnedButton implements ILocalRepaint, IButton {
    private boolean toggle = false;
    private boolean hovered = false;
    private boolean pressed = false;
    private final KeyAdapter enterToClick = new KeyAdapter() {
        @Override
        public void keyPressed(final KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                doClick();
            }
        }
    };

    /** Instantiates a new FButton. */
    public FButton() {
        this("");
    }

    public FButton(final String label) {
        super(label);
        this.setOpaque(false);
        this.setContentAreaFilled(false);
        this.setFocusPainted(false);
        this.setBorderPainted(false);
        this.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        this.setMargin(new Insets(0, 18, 0, 18));
        this.setFont(FDesign.font(14, true));
        this.setForeground(FDesign.TEXT_ON_ACCENT);
        this.setBackground(FDesign.ACCENT);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(final MouseEvent evt) {
                hovered = true;
                repaintSelf();
            }

            @Override
            public void mouseExited(final MouseEvent evt) {
                hovered = false;
                pressed = false;
                repaintSelf();
            }

            @Override
            public void mousePressed(final MouseEvent evt) {
                pressed = isEnabled();
                repaintSelf();
            }

            @Override
            public void mouseReleased(final MouseEvent evt) {
                pressed = false;
                repaintSelf();
            }
        });

        this.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(final FocusEvent e) {
                addKeyListener(enterToClick);
                repaintSelf();
            }

            @Override
            public void focusLost(final FocusEvent e) {
                removeKeyListener(enterToClick);
                repaintSelf();
            }
        });
    }

    @Override
    public void setEnabled(final boolean enabled) {
        if (!enabled) {
            hovered = false;
            pressed = false;
        }
        super.setEnabled(enabled);
        repaintSelf();
    }

    /** @return whether this button is permanently pressed, e.g. as a tab. */
    public boolean isToggled() {
        return toggle;
    }

    /** @param toggled whether the button should appear permanently pressed. */
    public void setToggled(final boolean toggled) {
        this.toggle = toggled;
        repaintSelf();
    }

    public int getAutoSizeWidth() {
        int width = 0;
        if (this.getText() != null && !this.getText().isEmpty()) {
            final FontMetrics metrics = this.getFontMetrics(this.getFont());
            width = metrics.stringWidth(this.getText());
        }
        return width;
    }

    /** Prevent button from repainting the whole screen. */
    @Override
    public void repaintSelf() {
        final Dimension d = getSize();
        repaint(0, 0, d.width, d.height);
    }

    @Override
    protected void paintComponent(final Graphics g) {
        final Graphics2D g2d = (Graphics2D) g.create();
        try {
            FDesign.drawButton(g2d, getWidth(), getHeight(), hovered, pressed,
                    toggle, isFocusOwner(), isEnabled());
            super.paintComponent(g2d);
        } finally {
            g2d.dispose();
        }
    }

    @Override
    public void setCommand(final UiCommand command) {
        addActionListener(e -> command.run());
    }

    @Override
    public void setImage(final FSkinProp color) {
        final Colors token = Colors.fromSkinProp(color);
        final SkinColor skinColor = token == null ? null : FSkin.getColor(token);
        if (skinColor != null) {
            setForeground(skinColor);
        }
    }

    @Override
    public void setTextColor(final int r, final int g, final int b) {
        setForeground(new Color(r, g, b));
    }
}
