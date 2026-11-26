package forge.screens.home;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.border.EmptyBorder;

import forge.toolbox.FDesign;
import forge.toolbox.FSkin.SkinnedLabel;

/** Standardized header label for the top of a submenu. */
@SuppressWarnings("serial")
public class LblHeader extends SkinnedLabel {
    /** @param txt0 header text. */
    public LblHeader(final String txt0) {
        super(txt0);
        setOpaque(false);
        setForeground(FDesign.TEXT);
        setFont(FDesign.font(16, true));
        setBorder(new EmptyBorder(0, 16, 0, 16));
    }

    @Override
    public void paintComponent(final Graphics g) {
        final Graphics2D g2d = (Graphics2D) g.create();
        try {
            g2d.setColor(FDesign.SURFACE_RAISED);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.setColor(FDesign.ACCENT);
            g2d.fillRect(0, 0, 4, getHeight());
            g2d.setColor(FDesign.BORDER);
            g2d.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
        } finally {
            g2d.dispose();
        }
        super.paintComponent(g);
    }
}
