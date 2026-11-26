package forge.screens.home;

import java.awt.Dimension;

import javax.swing.SwingUtilities;

import forge.toolbox.FButton;
import forge.toolbox.FDesign;
import forge.util.Localizer;

@SuppressWarnings("serial")
public class StartButton extends FButton {
    public StartButton() {
        super(Localizer.getInstance().getMessage("lblStart"));
        setFont(FDesign.font(16, true));
        setPreferredSize(new Dimension(180, 48));
        this.getAccessibleContext().setAccessibleName("Start game");

        addActionListener(e -> {
            setEnabled(false);

            // ensure the click action can resolve before allowing another click
            SwingUtilities.invokeLater(() -> setEnabled(true));
        });
    }
}
