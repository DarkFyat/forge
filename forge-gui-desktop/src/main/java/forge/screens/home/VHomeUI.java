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
package forge.screens.home;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import forge.Singletons;
import forge.gamemodes.tutorial.HowToPlay;
import forge.gui.framework.EDocID;
import forge.gui.framework.FScreen;
import forge.gui.framework.ICDoc;
import forge.gui.framework.ILocalRepaint;
import forge.gui.framework.IVTopLevelUI;
import forge.localinstance.properties.ForgePreferences.FPref;
import forge.model.FModel;
import forge.screens.home.gauntlet.VSubmenuGauntletBuild;
import forge.screens.home.gauntlet.VSubmenuGauntletCommanderBuild;
import forge.screens.home.gauntlet.VSubmenuGauntletCommanderQuick;
import forge.screens.home.gauntlet.VSubmenuGauntletContests;
import forge.screens.home.gauntlet.VSubmenuGauntletLoad;
import forge.screens.home.gauntlet.VSubmenuGauntletQuick;
import forge.screens.home.online.VSubmenuOnlineDecks;
import forge.screens.home.online.VSubmenuOnlineLobby;
import forge.screens.home.puzzle.VSubmenuPuzzleCreate;
import forge.screens.home.puzzle.VSubmenuPuzzleSolve;
import forge.screens.home.quest.VSubmenuChallenges;
import forge.screens.home.quest.VSubmenuDuels;
import forge.screens.home.quest.VSubmenuQuestDecks;
import forge.screens.home.quest.VSubmenuQuestDraft;
import forge.screens.home.quest.VSubmenuQuestLoadData;
import forge.screens.home.quest.VSubmenuQuestPrefs;
import forge.screens.home.quest.VSubmenuQuestStart;
import forge.screens.home.rogue.VSubmenuRogueMap;
import forge.screens.home.rogue.VSubmenuRogueStart;
import forge.screens.home.sanctioned.VSubmenuConstructed;
import forge.screens.home.sanctioned.VSubmenuDraft;
import forge.screens.home.sanctioned.VSubmenuSealed;
import forge.screens.home.settings.VSubmenuAchievements;
import forge.screens.home.settings.VSubmenuAvatars;
import forge.screens.home.settings.VSubmenuDownloaders;
import forge.screens.home.settings.VSubmenuPreferences;
import forge.screens.home.settings.VSubmenuReleaseNotes;
import forge.toolbox.FDesign;
import forge.toolbox.FScrollPanel;
import forge.toolbox.FSkin.SkinnedPanel;
import forge.util.Localizer;
import forge.view.FView;
import net.miginfocom.swing.MigLayout;

/**
 * Top level view class for home UI drag layout.
 *
 * <br><br><i>(V at beginning of class name denotes a view class.)</i>
 */
@SuppressWarnings("serial")
public enum VHomeUI implements IVTopLevelUI {
    /** */
    SINGLETON_INSTANCE;

    private static final int BRAND_HEIGHT = 88;
    private static final int ACTION_HEIGHT = 38;
    private static final int HEADER_HEIGHT = BRAND_HEIGHT + ACTION_HEIGHT + 24;

    private final List<IVSubmenu<? extends ICDoc>> allSubmenus = new ArrayList<>();
    private final Map<EDocID, LblMenuItem> allSubmenuLabels = new HashMap<>();
    private final Map<EMenuGroup, LblGroup> allGroupLabels = new HashMap<>();

    private final PnlMenu pnlMenu = new PnlMenu();
    private final PnlDisplay pnlDisplay = new PnlDisplay();
    private final FScrollPanel pnlSubmenus;

    private final JLabel lblLogo = new BrandMark();
    /** Starts the guided practice game that teaches a newcomer the basics. */
    private final JButton lblHowToPlay = new ActionLink(
            Localizer.getInstance().getMessage("lblHowToPlay"),
            Localizer.getInstance().getMessage("lblHowToPlayTooltip"));

    VHomeUI() {
        final JPanel pnlMainMenu = new JPanel(new MigLayout("w 100%!, ax center, insets 14px 12px 8px 12px, gap 0, wrap"));
        pnlMainMenu.setOpaque(false);

        pnlMainMenu.add(lblLogo, "w 100%!, h " + BRAND_HEIGHT + "px!");
        pnlMainMenu.add(lblHowToPlay, "w 100%!, h " + ACTION_HEIGHT + "px!, gaptop 8px");
        pnlMenu.add(pnlMainMenu);

        pnlSubmenus = new FScrollPanel(new MigLayout("insets 8px 10px 12px 10px, gap 0, wrap, hidemode 3"), true,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // Add new menu items here (order doesn't matter).
        allSubmenus.add(VSubmenuConstructed.SINGLETON_INSTANCE);
        allSubmenus.add(VSubmenuDraft.SINGLETON_INSTANCE);
        allSubmenus.add(VSubmenuSealed.SINGLETON_INSTANCE);

        allSubmenus.add(VSubmenuOnlineLobby.SINGLETON_INSTANCE);
        allSubmenus.add(VSubmenuOnlineDecks.SINGLETON_INSTANCE);

        allSubmenus.add(VSubmenuQuestStart.SINGLETON_INSTANCE);
        allSubmenus.add(VSubmenuQuestLoadData.SINGLETON_INSTANCE);
        allSubmenus.add(VSubmenuQuestDecks.SINGLETON_INSTANCE);
        allSubmenus.add(VSubmenuDuels.SINGLETON_INSTANCE);
        allSubmenus.add(VSubmenuChallenges.SINGLETON_INSTANCE);
        allSubmenus.add(VSubmenuQuestDraft.SINGLETON_INSTANCE);
        allSubmenus.add(VSubmenuQuestPrefs.SINGLETON_INSTANCE);

        allSubmenus.add(VSubmenuGauntletQuick.SINGLETON_INSTANCE);
        allSubmenus.add(VSubmenuGauntletBuild.SINGLETON_INSTANCE);
        allSubmenus.add(VSubmenuGauntletLoad.SINGLETON_INSTANCE);
        allSubmenus.add(VSubmenuGauntletContests.SINGLETON_INSTANCE);
        allSubmenus.add(VSubmenuGauntletCommanderQuick.SINGLETON_INSTANCE);
        allSubmenus.add(VSubmenuGauntletCommanderBuild.SINGLETON_INSTANCE);

        allSubmenus.add(VSubmenuRogueStart.SINGLETON_INSTANCE);
        allSubmenus.add(VSubmenuRogueMap.SINGLETON_INSTANCE);

        allSubmenus.add(VSubmenuPuzzleSolve.SINGLETON_INSTANCE);
        allSubmenus.add(VSubmenuPuzzleCreate.SINGLETON_INSTANCE);

        allSubmenus.add(VSubmenuPreferences.SINGLETON_INSTANCE);
        allSubmenus.add(VSubmenuAchievements.SINGLETON_INSTANCE);
        allSubmenus.add(VSubmenuAvatars.SINGLETON_INSTANCE);
        allSubmenus.add(VSubmenuDownloaders.SINGLETON_INSTANCE);
        allSubmenus.add(VSubmenuReleaseNotes.SINGLETON_INSTANCE);

        // For each group: init its panel
        final SortedMap<EMenuGroup, JPanel> allGroupPanels = new TreeMap<>();
        for (final EMenuGroup e : EMenuGroup.values()) {
            allGroupPanels.put(e, new PnlGroup());
            allGroupPanels.get(e).setVisible(false);
            allGroupPanels.get(e).setLayout(new MigLayout("insets 0, gap 0, wrap"));
            allGroupPanels.get(e).setName(e.toString());
        }

        // For each item: Add to its group, and add to the card layout in right panel.
        for (final IVSubmenu<? extends ICDoc> item : allSubmenus) {
            allSubmenuLabels.put(item.getItemEnum(), new LblMenuItem(item));
            allGroupPanels.get(item.getGroupEnum()).add(
                    allSubmenuLabels.get(item.getItemEnum()), "w 100%!, h 34px!, gap 0 0 2px 2px");
        }

        // For each group: add its title, then its panel, then "click" if necessary.
        for (final EMenuGroup e : allGroupPanels.keySet()) {
            allGroupLabels.put(e, new LblGroup(e));
            pnlSubmenus.add(allGroupLabels.get(e), "w 100%!, h 34px!, gap 0 0 4px 4px");
            pnlSubmenus.add(allGroupPanels.get(e), "w 100%!, gap 0 0 0 0");

            // Expand groups expanded from previous session
            if (FModel.getPreferences().getPrefBoolean(FPref.valueOf("SUBMENU_" + e.toString()))) {
                allGroupLabels.get(e).groupClick(e);
            }
        }

        pnlMenu.add(pnlSubmenus, "w 100%!, h 100% - " + HEADER_HEIGHT + "px!");
        pnlDisplay.setBackground(FDesign.BASE);
    }

    /** @return {@link javax.swing.JPanel} */
    public JPanel getPnlMenu() {
        return pnlMenu;
    }

    public void setSidebarVisible(final boolean visible) {
        if (pnlMenu.isVisible() != visible) {
            pnlMenu.setVisible(visible);
            final JPanel content = FView.SINGLETON_INSTANCE.getPnlInsets();
            content.revalidate();
            content.repaint();
        }
    }

    /** @return {@link javax.swing.JPanel} */
    public PnlDisplay getPnlDisplay() {
        return pnlDisplay;
    }

    /** @return {@link forge.toolbox.FScrollPanel} */
    public FScrollPanel getPnlSubmenus() {
        return pnlSubmenus;
    }

    /** @return Map of document IDs to their sidebar labels. */
    public Map<EDocID, LblMenuItem> getAllSubmenuLabels() {
        return allSubmenuLabels;
    }

    @Override
    public void instantiate() {
    }

    @Override
    public void populate() {
        JPanel pnl = FView.SINGLETON_INSTANCE.getPnlInsets();
        pnl.setBorder(null);
        pnl.setLayout(new MigLayout("insets 0, gap 0, fill, hidemode 3"));

        pnl.add(pnlMenu, "w " + FDesign.SIDEBAR_WIDTH + "px!, h 100%!");
        pnl.add(pnlDisplay, "wmin 0, grow, push");
    }

    /** The content canvas behind every home submenu. */
    public class PnlDisplay extends SkinnedPanel implements ILocalRepaint {
        /** Constructor. */
        public PnlDisplay() {
            this.setOpaque(false);
        }

        @Override
        public void repaintSelf() {
            final Dimension d = this.getSize();
            repaint(0, 0, d.width, d.height);
        }

        @Override
        public void paintComponent(final Graphics g) {
            super.paintComponent(g);
            final Graphics2D g2d = (Graphics2D) g.create();
            try {
                g2d.setPaint(new GradientPaint(0, 0, FDesign.BASE,
                        0, getHeight(), FDesign.SURFACE));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            } finally {
                g2d.dispose();
            }
        }
    }

    /** Flat sidebar with a single hairline divider. */
    private class PnlMenu extends JPanel {
        public PnlMenu() {
            this.setLayout(new MigLayout("insets 0, gap 0, wrap, hidemode 3"));
            this.setOpaque(false);
        }

        @Override
        public void paintComponent(final Graphics g) {
            super.paintComponent(g);
            final Graphics2D g2d = (Graphics2D) g.create();
            try {
                g2d.setColor(FDesign.SIDEBAR);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(FDesign.BORDER);
                g2d.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
            } finally {
                g2d.dispose();
            }
        }
    }

    /** A code-drawn wordmark, replacing the low-resolution v2 anvil sprite. */
    private static class BrandMark extends JLabel {
        private BrandMark() {
            setOpaque(false);
            setPreferredSize(new Dimension(220, BRAND_HEIGHT));
        }

        @Override
        protected void paintComponent(final Graphics g) {
            super.paintComponent(g);
            final Graphics2D g2d = (Graphics2D) g.create();
            try {
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // A compact, geometric anvil mark.
                g2d.setColor(FDesign.ACCENT);
                g2d.fillRoundRect(14, 25, 46, 7, 4, 4);
                g2d.fillRoundRect(44, 14, 11, 28, 4, 4);
                g2d.fillRoundRect(8, 36, 58, 7, 4, 4);
                g2d.fillRoundRect(20, 44, 34, 21, 7, 7);
                g2d.fillRoundRect(12, 67, 50, 6, 3, 3);

                g2d.setColor(FDesign.TEXT);
                g2d.setFont(FDesign.font(27, true));
                g2d.drawString("FORGE", 82, 44);
                g2d.setColor(FDesign.TEXT_MUTED);
                g2d.setFont(FDesign.font(9, false));
                g2d.drawString("MAGIC  •  RULES ENGINE", 84, 62);
                g2d.setColor(FDesign.withAlpha(FDesign.ACCENT, 150));
                g2d.fillRoundRect(84, 70, 82, 2, 2, 2);
            } finally {
                g2d.dispose();
            }
        }
    }

    /** A compact primary action rendered as a flat rounded link. */
    private static class ActionLink extends JButton {
        private boolean hovered;

        private ActionLink(final String text, final String tooltip) {
            super(text);
            setHorizontalAlignment(SwingConstants.CENTER);
            setToolTipText(tooltip);
            setOpaque(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            addActionListener(e -> HowToPlay.chooseAndStart());
            setFont(FDesign.font(13, true));
            setForeground(FDesign.ACCENT);
            setBorder(new EmptyBorder(8, 12, 8, 12));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(final MouseEvent e) {
                    hovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(final MouseEvent e) {
                    hovered = false;
                    repaint();
                }

            });
        }

        @Override
        protected void paintComponent(final Graphics g) {
            final Graphics2D g2d = (Graphics2D) g.create();
            try {
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(hovered ? FDesign.SURFACE_SOFT : FDesign.SURFACE_RAISED);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), FDesign.RADIUS, FDesign.RADIUS);
                g2d.setColor(isFocusOwner() ? FDesign.FOCUS : hovered ? FDesign.ACCENT : FDesign.BORDER);
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, FDesign.RADIUS, FDesign.RADIUS);
            } finally {
                g2d.dispose();
            }
            super.paintComponent(g);
        }
    }

    @Override
    public boolean onSwitching(final FScreen fromScreen, final FScreen toScreen) {
        return true;
    }

    @Override
    public boolean onClosing(final FScreen screen) {
        Singletons.getControl().exitForge();
        return false; //don't allow closing Home tab
    }
}
