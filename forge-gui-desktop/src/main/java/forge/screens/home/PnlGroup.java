package forge.screens.home;

import java.awt.Graphics;
import java.awt.Graphics2D;

import forge.toolbox.FDesign;
import forge.toolbox.FSkin.SkinnedPanel;
import net.miginfocom.swing.MigLayout;

/** Custom panel for containing LblMenuItem components. */
@SuppressWarnings("serial")
public class PnlGroup extends SkinnedPanel {
    /** Constructor. */
    public PnlGroup() {
        this.setLayout(new MigLayout("insets 4px 0 8px 0, gap 0, wrap"));
        this.setOpaque(false);
    }

    @Override
    public void paintComponent(final Graphics g) {
        final Graphics2D g2d = (Graphics2D) g.create();
        try {
            g2d.setColor(FDesign.SIDEBAR);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        } finally {
            g2d.dispose();
        }
        super.paintComponent(g);
    }
}
