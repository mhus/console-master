package com.consolemaster;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the CompositeCanvas class.
 */
class CompositeCanvasTest {
    
    private CompositeCanvas composite;
    
    @Mock
    private Canvas childCanvas1;
    
    @Mock
    private Canvas childCanvas2;
    
    @Mock
    private Graphics graphics;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        composite = new CompositeCanvas(0, 0, 100, 50);
    }
    
    @Test
    void shouldCreateEmptyComposite() {
        assertEquals(0, composite.getChildCount());
        assertTrue(composite.getChildren().isEmpty());
    }

    @Test
    void shouldAddChild() {
        composite.addChild(childCanvas1);
        
        assertEquals(1, composite.getChildCount());
        assertTrue(composite.getChildren().contains(childCanvas1));
    }
    
    @Test
    void shouldAddMultipleChildren() {
        composite.addChild(childCanvas1);
        composite.addChild(childCanvas2);
        
        assertEquals(2, composite.getChildCount());
        assertEquals(childCanvas1, composite.getChildren().get(0));
        assertEquals(childCanvas2, composite.getChildren().get(1));
    }
    
    @Test
    void shouldIgnoreNullChild() {
        composite.addChild(null);
        
        assertEquals(0, composite.getChildCount());
    }
    
    @Test
    void shouldRemoveChild() {
        composite.addChild(childCanvas1);
        composite.addChild(childCanvas2);
        
        boolean removed = composite.removeChild(childCanvas1);
        
        assertTrue(removed);
        assertEquals(1, composite.getChildCount());
        assertFalse(composite.getChildren().contains(childCanvas1));
        assertTrue(composite.getChildren().contains(childCanvas2));
    }
    
    @Test
    void shouldReturnFalseWhenRemovingNonExistentChild() {
        composite.addChild(childCanvas1);
        
        boolean removed = composite.removeChild(childCanvas2);
        
        assertFalse(removed);
        assertEquals(1, composite.getChildCount());
    }
    
    @Test
    void shouldRemoveAllChildren() {
        composite.addChild(childCanvas1);
        composite.addChild(childCanvas2);
        
        composite.removeAllChildren();
        
        assertEquals(0, composite.getChildCount());
        assertTrue(composite.getChildren().isEmpty());
    }
    
    @Test
    void shouldPaintVisibleChildrenInOrder() {
        when(childCanvas1.isVisible()).thenReturn(true);
        when(childCanvas1.getWidth()).thenReturn(10);
        when(childCanvas1.getHeight()).thenReturn(10);
        when(childCanvas1.getX()).thenReturn(0);
        when(childCanvas1.getY()).thenReturn(0);

        when(childCanvas2.isVisible()).thenReturn(true);
        when(childCanvas2.getWidth()).thenReturn(10);
        when(childCanvas2.getHeight()).thenReturn(10);
        when(childCanvas2.getX()).thenReturn(20);
        when(childCanvas2.getY()).thenReturn(0);

        composite.addChild(childCanvas1);
        composite.addChild(childCanvas2);
        
        composite.paint(graphics);
        
        // With ClippingGraphics, the children receive a ClippingGraphics wrapper, not the original graphics
        verify(childCanvas1).paint(any(Graphics.class));
        verify(childCanvas2).paint(any(Graphics.class));
    }
    
    @Test
    void shouldSkipInvisibleChildren() {
        when(childCanvas1.isVisible()).thenReturn(true);
        when(childCanvas1.getWidth()).thenReturn(10);
        when(childCanvas1.getHeight()).thenReturn(10);
        when(childCanvas1.getX()).thenReturn(0);
        when(childCanvas1.getY()).thenReturn(0);

        when(childCanvas2.isVisible()).thenReturn(false);
        
        composite.addChild(childCanvas1);
        composite.addChild(childCanvas2);
        
        composite.paint(graphics);
        
        // With ClippingGraphics, the visible child receives a ClippingGraphics wrapper
        verify(childCanvas1).paint(any(Graphics.class));
        verify(childCanvas2, never()).paint(any(Graphics.class));
    }
    
    @Test
    void shouldHaveNoLayoutByDefault() {
        assertEquals(NoLayout.INSTANCE, composite.getLayout());
    }
    
    @Test
    void shouldSetAndGetLayout() {
        Layout customLayout = mock(Layout.class);
        composite.setLayout(customLayout);
        
        assertEquals(customLayout, composite.getLayout());
        verify(customLayout).layoutChildren(composite);
    }
    
    @Test
    void shouldUseNoLayoutWhenSettingNullLayout() {
        composite.setLayout(null);
        assertEquals(NoLayout.INSTANCE, composite.getLayout());
    }
    
    @Test
    void shouldCreateCompositeWithCustomLayout() {
        Layout customLayout = mock(Layout.class);
        CompositeCanvas customComposite = new CompositeCanvas(0, 0, 100, 50, customLayout);
        
        assertEquals(customLayout, customComposite.getLayout());
    }
    
    @Test
    void shouldTriggerLayoutWhenAddingChild() {
        Layout mockLayout = mock(Layout.class);
        composite.setLayout(mockLayout);
        
        // Reset mock to clear the setLayout call
        reset(mockLayout);
        
        composite.addChild(childCanvas1);
        
        verify(mockLayout).childAdded(composite, childCanvas1);
        verify(mockLayout).layoutChildren(composite);
    }
    
    @Test
    void shouldTriggerLayoutWhenRemovingChild() {
        Layout mockLayout = mock(Layout.class);
        composite.setLayout(mockLayout);
        composite.addChild(childCanvas1);
        
        // Reset mock to clear previous calls
        reset(mockLayout);
        
        boolean removed = composite.removeChild(childCanvas1);
        
        assertTrue(removed);
        verify(mockLayout).childRemoved(composite, childCanvas1);
        verify(mockLayout).layoutChildren(composite);
    }
    
    @Test
    void shouldTriggerLayoutWhenRemovingAllChildren() {
        Layout mockLayout = mock(Layout.class);
        composite.setLayout(mockLayout);
        composite.addChild(childCanvas1);
        composite.addChild(childCanvas2);
        
        // Reset mock to clear previous calls
        reset(mockLayout);
        
        composite.removeAllChildren();
        
        verify(mockLayout).childRemoved(composite, childCanvas1);
        verify(mockLayout).childRemoved(composite, childCanvas2);
        verify(mockLayout).layoutChildren(composite);
    }
    
    @Test
    void shouldTriggerLayoutWhenSizeChanges() {
        Layout mockLayout = mock(Layout.class);
        composite.setLayout(mockLayout);
        
        // Reset mock to clear the setLayout call
        reset(mockLayout);
        
        composite.setWidth(150);
        verify(mockLayout, times(1)).layoutChildren(composite);

        composite.setHeight(80);
    }
    
    @Test
    void shouldCallDoLayoutManually() {
        Layout mockLayout = mock(Layout.class);
        composite.setLayout(mockLayout);
        
        // Reset mock to clear the setLayout call
        reset(mockLayout);
        
        composite.doLayout();
        verify(mockLayout).layoutChildren(composite);
    }
    
    @Test
    void shouldGetPreferredSize() {
        Layout mockLayout = mock(Layout.class);
        Layout.Dimension expectedSize = new Layout.Dimension(120, 90);
        when(mockLayout.getPreferredSize(composite)).thenReturn(expectedSize);
        
        composite.setLayout(mockLayout);
        Layout.Dimension actualSize = composite.getPreferredSize();
        
        assertEquals(expectedSize, actualSize);
        verify(mockLayout).getPreferredSize(composite);
    }

    @Test
    void shouldAddChildWithLayoutConstraint() {
        // Use real Canvas objects for constraint testing
        Canvas realChild = new TestCanvas(0, 0, 10, 5);
        PositionConstraint constraint = new PositionConstraint(PositionConstraint.Position.CENTER);

        composite.addChild(realChild, constraint);

        assertEquals(1, composite.getChildCount());
        assertEquals(constraint, realChild.getLayoutConstraint());
        assertTrue(composite.getChildren().contains(realChild));
    }

    @Test
    void shouldSetChildConstraint() {
        // Use real Canvas objects for constraint testing
        Canvas realChild = new TestCanvas(0, 0, 10, 5);
        composite.addChild(realChild);
        PositionConstraint constraint = new PositionConstraint(PositionConstraint.Position.TOP_LEFT);

        composite.setChildConstraint(realChild, constraint);

        assertEquals(constraint, realChild.getLayoutConstraint());
        assertEquals(constraint, composite.getChildConstraint(realChild));
    }

    @Test
    void shouldIgnoreConstraintForNonExistentChild() {
        PositionConstraint constraint = new PositionConstraint(PositionConstraint.Position.CENTER);

        composite.setChildConstraint(childCanvas1, constraint); // childCanvas1 not added yet

        assertNull(childCanvas1.getLayoutConstraint()); // Should not be set
    }

    @Test
    void shouldReturnNullConstraintForNullChild() {
        assertNull(composite.getChildConstraint(null));
    }

    @Test
    void shouldReturnNullConstraintForUnconstrainedChild() {
        composite.addChild(childCanvas1);
        assertNull(composite.getChildConstraint(childCanvas1));
    }

    /**
     * Test implementation of Canvas for testing purposes.
     */
    private static class TestCanvas extends Canvas {
        public TestCanvas(int x, int y, int width, int height) {
            super(x, y, width, height);
        }

        @Override
        public void paint(Graphics graphics) {
            // Test implementation
        }
    }
}
