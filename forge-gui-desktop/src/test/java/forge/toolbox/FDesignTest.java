package forge.toolbox;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.SwingUtilities;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

/** Headless regression checks for the desktop design primitives and buttons. */
public class FDesignTest {
    @Test
    public void mapsLegacySkinColors() {
        for (FSkin.Colors color : FSkin.Colors.values()) {
            assertNotNull("No design color for " + color, FDesign.colorFor(color));
        }
        assertTrue(FDesign.isBuiltInTheme("Default"));
        assertFalse(FDesign.isBuiltInTheme("custom"));
        assertEquals(255, FDesign.withAlpha(Color.RED, 300).getAlpha());
        assertEquals(0, FDesign.withAlpha(Color.RED, -1).getAlpha());
    }

    @Test
    public void buttonStatesAndCommandWork() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            final FButton button = new FButton("Play");
            button.setSize(180, 48);
            final AtomicInteger calls = new AtomicInteger();
            button.setCommand(() -> calls.incrementAndGet());
            button.doClick();
            assertEquals(1, calls.get());

            final int normal = render(button);
            button.dispatchEvent(new MouseEvent(button, MouseEvent.MOUSE_ENTERED,
                    System.currentTimeMillis(), 0, 5, 5, 0, false));
            final int hover = render(button);
            assertTrue("Hover must change the button face", normal != hover);

            button.setToggled(true);
            final int toggled = render(button);
            assertTrue("Toggle must change the button face", normal != toggled);
            button.setEnabled(false);
            assertTrue("Disabled must change the button face", toggled != render(button));
            button.doClick();
            assertEquals("Disabled buttons must not execute commands", 1, calls.get());
        });
    }

    private static int render(final FButton button) {
        final BufferedImage image = new BufferedImage(180, 48, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphics = image.createGraphics();
        try {
            button.paint(graphics);
        } finally {
            graphics.dispose();
        }
        return image.getRGB(30, 24);
    }
}
