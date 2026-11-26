package forge.screens.home;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.border.EmptyBorder;

import forge.gui.framework.ICDoc;
import forge.gui.framework.ILocalRepaint;
import forge.toolbox.FDesign;
import forge.toolbox.FSkin.SkinnedLabel;

/** Custom label for an item in the home sidebar. */
@SuppressWarnings("serial")
public class LblMenuItem extends SkinnedLabel implements ILocalRepaint {
    private boolean selected = false;
    private boolean hovered = false;

    /** @param doc0 submenu represented by this label. */
    public LblMenuItem(final IVSubmenu<? extends ICDoc> doc0) {
        super(doc0.getMenuTitle());
        setOpaque(false);
        setFont(FDesign.font(13, false));
        setForeground(FDesign.TEXT_MUTED);
        setBorder(new EmptyBorder(0, 16, 0, 12));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
                CHomeUI.SINGLETON_INSTANCE.itemClick(doc0.getDocumentID());
            }

            @Override
            public void mouseEntered(final MouseEvent e) {
                hovered = true;
                repaintSelf();
            }

            @Override
            public void mouseExited(final MouseEvent e) {
                hovered = false;
                repaintSelf();
            }
        });
    }

    /** @param selected whether this item represents the active submenu. */
    public void setSelected(final boolean selected) {
        this.selected = selected;
        setFont(FDesign.font(13, selected));
        setForeground(selected ? FDesign.TEXT : FDesign.TEXT_MUTED);
        repaintSelf();
    }

    @Override
    public void repaintSelf() {
        final Dimension d = this.getSize();
        repaint(0, 0, d.width, d.height);
    }

    @Override
    public void paintComponent(final Graphics g) {
        FDesign.drawSidebarItem((Graphics2D) g, getWidth(), getHeight(), selected, hovered);
        super.paintComponent(g);
    }
}
