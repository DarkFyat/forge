package forge.screens.home;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.border.EmptyBorder;

import forge.gui.framework.ILocalRepaint;
import forge.localinstance.properties.ForgePreferences.FPref;
import forge.model.FModel;
import forge.toolbox.FDesign;
import forge.toolbox.FSkin.SkinnedLabel;

/** Custom label for the title of a menu item group. */
@SuppressWarnings("serial")
public class LblGroup extends SkinnedLabel implements ILocalRepaint {
    private static final boolean isCompactMenu = FModel.getPreferences().getPrefBoolean(FPref.UI_COMPACT_MAIN_MENU);
    private static EMenuGroup activeMenuGroup = null;

    private boolean hovered = false;

    /** @param e0 group represented by this label. */
    public LblGroup(final EMenuGroup e0) {
        super(e0.getTitle());
        setOpaque(false);
        setFont(FDesign.font(11, true));
        setForeground(FDesign.TEXT_MUTED);
        setBorder(new EmptyBorder(0, 14, 0, 12));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent evt) {
                groupClick(e0);
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

    /** Handler for click on group title. */
    public void groupClick(final EMenuGroup menuGroup) {
        toggleMenuGroupCollapseState(menuGroup);
    }

    private void toggleMenuGroupCollapseState(final EMenuGroup menuGroup) {
        if (isCompactMenu) {
            if (menuGroup != activeMenuGroup) {
                if (activeMenuGroup != null) {
                    setMenuGroupCollapseState(activeMenuGroup);
                }
                setMenuGroupCollapseState(menuGroup);
                activeMenuGroup = menuGroup;
            }
        } else {
            setMenuGroupCollapseState(menuGroup);
            activeMenuGroup = menuGroup;
        }
    }

    private void setMenuGroupCollapseState(final EMenuGroup e0) {
        final Component[] menuObjects = this.getParent().getComponents();

        // Toggle label items in this group
        for (final Component c : menuObjects) {
            if (c.getName() != null && c.getName().equals(e0.toString())) {
                if (c.isVisible()) {
                    c.setVisible(false);
                    FModel.getPreferences().setPref(FPref.valueOf("SUBMENU_" + e0.toString()), "false");
                } else {
                    c.setVisible(true);
                    FModel.getPreferences().setPref(FPref.valueOf("SUBMENU_" + e0.toString()), "true");
                }

                FModel.getPreferences().save();
                break;
            }
        }
    }

    @Override
    public void repaintSelf() {
        final Dimension d = this.getSize();
        repaint(0, 0, d.width, d.height);
    }

    @Override
    public void paintComponent(final Graphics g) {
        final Graphics2D g2d = (Graphics2D) g.create();
        try {
            g2d.setColor(hovered ? FDesign.HOVER : FDesign.SIDEBAR);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.setColor(FDesign.BORDER);
            g2d.drawLine(12, getHeight() - 1, getWidth() - 12, getHeight() - 1);
        } finally {
            g2d.dispose();
        }
        super.paintComponent(g);
    }
}
