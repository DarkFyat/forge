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
package forge.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import forge.localinstance.properties.ForgePreferences.FPref;
import forge.localinstance.skin.FSkinProp;
import forge.model.FModel;
import forge.toolbox.FDesign;
import forge.toolbox.FProgressBar;
import forge.toolbox.FSkin;
import forge.toolbox.FSkin.SkinIcon;
import forge.toolbox.FSkin.SkinnedLabel;

/** Shows the splash frame as the application starts. */
@SuppressWarnings("serial")
public class SplashFrame extends JFrame {
    private FProgressBar barProgress;

    private final MouseAdapter madClose = new MouseAdapter() {
        @Override
        public void mouseEntered(final MouseEvent evt) {
            final JButton button = (JButton) evt.getSource();
            button.setForeground(FDesign.ACCENT);
            button.setBorder(BorderFactory.createLineBorder(FDesign.ACCENT));
        }

        @Override
        public void mouseExited(final MouseEvent evt) {
            final JButton button = (JButton) evt.getSource();
            button.setForeground(FDesign.TEXT_MUTED);
            button.setBorder(BorderFactory.createLineBorder(FDesign.BORDER));
        }
    };

    private final Action actClose = new AbstractAction() {
        @Override
        public void actionPerformed(final ActionEvent e) {
            System.exit(0);
        }
    };

    /** Create the frame; this must be called outside the EDT. */
    public SplashFrame() {
        super();
        FSkin.loadLight(FModel.getPreferences().getPref(FPref.UI_SKIN), true);

        try {
            SwingUtilities.invokeAndWait(SplashFrame.this::init);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        final SkinIcon bgIcon = FSkin.getIcon(FSkinProp.BG_SPLASH);
        barProgress = new FProgressBar();

        this.setUndecorated(true);
        this.setMinimumSize(new Dimension(450, 450));
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Loading Forge...");

        final JPanel pnlContent = new JPanel();
        this.setContentPane(pnlContent);
        pnlContent.setLayout(null);
        pnlContent.setBackground(FDesign.BASE);

        final JLabel lblDisclaimer = new JLabel("<html><center>"
                + "Forge is not affiliated in any way with Wizards of the Coast."
                + "<br>Forge is open source software, released under "
                + "the GNU General Public License.</center></html>");

        lblDisclaimer.setBounds(24, 300, 402, 34);
        lblDisclaimer.setFont(FDesign.font(10, false));
        lblDisclaimer.setHorizontalAlignment(SwingConstants.CENTER);
        lblDisclaimer.setForeground(FDesign.TEXT_MUTED);
        pnlContent.add(lblDisclaimer);

        final JButton btnClose = new JButton("×");
        btnClose.setBounds(410, 14, 26, 26);
        btnClose.setForeground(FDesign.TEXT_MUTED);
        btnClose.setFont(FDesign.font(16, false));
        btnClose.setBackground(FDesign.SURFACE_RAISED);
        btnClose.setBorder(BorderFactory.createLineBorder(FDesign.BORDER));
        btnClose.setFocusPainted(false);
        pnlContent.add(btnClose);

        btnClose.addMouseListener(madClose);
        btnClose.addActionListener(actClose);
        pnlContent.getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), "escAction");
        pnlContent.getActionMap().put("escAction", actClose);

        barProgress.setString("Preparing your multiverse");
        barProgress.setBounds(24, 373, 402, 48);
        pnlContent.add(barProgress);

        final SkinnedLabel bgLabel = new SkinnedLabel();
        bgLabel.setIcon(bgIcon);
        bgLabel.setBounds(0, 0, 450, 450);
        pnlContent.add(bgLabel);

        this.pack();
        this.setVisible(true);
    }

    /** @return {@link forge.toolbox.FProgressBar} */
    public FProgressBar getProgressBar() {
        return this.barProgress;
    }
}
