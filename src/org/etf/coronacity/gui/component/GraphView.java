package org.etf.coronacity.gui.component;

import org.etf.coronacity.helper.Colors;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/*
 * Component that represents stepped graph
 * This class has one constructor which expects three values
 *      ArrayList<Long> - values that go on the x axis
 *      ArrayList<Long> - values that go on the y axis
 *      String - string which is code of color for the graph
 */
public class GraphView extends JComponent {

    private ArrayList<Long> xValues;
    private ArrayList<Long> yValues;
    private String color;

    private static final int PADDING = 8;
    private static final int LABEL_PADDING = 16;
    private static final int POINT_WIDTH = 6;
    private static final Stroke GRAPH_STROKE = new BasicStroke(2f);

    public GraphView(ArrayList<Long> xValues, ArrayList<Long> yValues, String color) {
        this.xValues = xValues;
        this.yValues = yValues;
        this.color = color;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D graphics2D = (Graphics2D) g;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // draw white background
        drawBackground(graphics2D);

        // create hatch marks and grid lines for y axis.
        createGridForY(graphics2D);

        // and for x axis
        createGridForX(graphics2D);

        // create x and y axes
        plotAxes(graphics2D);

        // plot data
        plotData(graphics2D);
    }

    //
    // private
    //

    private void drawBackground(Graphics2D graphics2D) {
        graphics2D.setColor(Color.decode(Colors.COLOR_GRAPH_BACKGROUND));
        graphics2D.fillRect(PADDING + LABEL_PADDING, PADDING, getWidth() - 2 * PADDING - LABEL_PADDING, getHeight() - 2 * PADDING - LABEL_PADDING);
    }

    private void createGridForY(Graphics2D graphics2D) {

        int size = getMaxValue(yValues);

        if (size == 0)
            size = 1;

        for (int i = 1; i <= size; i++) {

            int x0 = PADDING + LABEL_PADDING;
            int x1 = POINT_WIDTH + PADDING + LABEL_PADDING;
            int y0 = getHeight() - ((i * (getHeight() - PADDING * 2 - LABEL_PADDING)) / size + PADDING + LABEL_PADDING);

            graphics2D.setColor(Color.decode(Colors.COLOR_GRAPH_GRID));
            graphics2D.drawLine(PADDING + LABEL_PADDING + 1 + POINT_WIDTH, y0, getWidth() - PADDING, y0);
            graphics2D.setColor(Color.BLACK);

            String yLabel = String.valueOf(i);
            FontMetrics metrics = graphics2D.getFontMetrics();

            int labelWidth = metrics.stringWidth(yLabel);
            graphics2D.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);

            graphics2D.drawLine(x0, y0, x1, y0);
        }
    }

    private void createGridForX(Graphics2D graphics2D) {

        for (int i = 0; i < xValues.size(); i++) {

            int x0 = i * (getWidth() - PADDING * 2 - LABEL_PADDING) / (xValues.size() - 1) + PADDING + LABEL_PADDING;
            int y0 = getHeight() - PADDING - LABEL_PADDING;
            int y1 = y0 - POINT_WIDTH;

            if ((i % ((int) ((xValues.size() / 20.0)) + 1)) == 0) {

                graphics2D.setColor(Color.decode(Colors.COLOR_GRAPH_GRID));
                graphics2D.drawLine(x0, getHeight() - PADDING - LABEL_PADDING - 1 - POINT_WIDTH, x0, PADDING);
                graphics2D.setColor(Color.BLACK);

                String xLabel = String.valueOf(xValues.get(i));
                FontMetrics metrics = graphics2D.getFontMetrics();
                int labelWidth = metrics.stringWidth(xLabel);

                graphics2D.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
            }

            graphics2D.drawLine(x0, y0, x0, y1);
        }
    }

    private void plotAxes(Graphics2D graphics2D) {
        graphics2D.drawLine(PADDING + LABEL_PADDING, getHeight() - PADDING - LABEL_PADDING, PADDING + LABEL_PADDING, PADDING);
        graphics2D.drawLine(PADDING + LABEL_PADDING, getHeight() - PADDING - LABEL_PADDING, getWidth() - PADDING, getHeight() - PADDING - LABEL_PADDING);
    }

    private void plotData(Graphics2D graphics2D) {

        for (int i = 0; i < xValues.size(); i++) {

            double xScale = ((double) getWidth() - 2 * PADDING - LABEL_PADDING) / getMaxValue(xValues);
            double yScale = ((double) getHeight() - 2 * PADDING - LABEL_PADDING) / getMaxValue(yValues);

            ArrayList<Point> points = new ArrayList<>();
            for (int j = 0; j < xValues.size(); j++) {

                int x = (int) (xValues.get(j) * xScale + PADDING + LABEL_PADDING);
                int y;

                if (yValues.get(j) != 0)
                    y = (int) ((getHeight() - LABEL_PADDING) - (yValues.get(j) * yScale + PADDING));
                else
                    y = getHeight() - PADDING - LABEL_PADDING;

                points.add(new Point(x, y));
            }

            Stroke oldStroke = graphics2D.getStroke();
            graphics2D.setColor(Color.decode(color));
            graphics2D.setStroke(GRAPH_STROKE);
            for (int j = 0; j < points.size() - 1; j++) {

                int x1 = points.get(j).x;
                int y1 = points.get(j).y;
                int x2 = points.get(j + 1).x;
                int y2 = points.get(j + 1).y;

                graphics2D.drawLine(x1, y1, x2, y1);
                graphics2D.drawLine(x2, y1, x2, y2);
            }

            graphics2D.setStroke(oldStroke);
            graphics2D.setColor(Color.decode(Colors.COLOR_GRAPH_POINT));

            for (Point point : points) {

                int x = point.x - POINT_WIDTH / 2;
                int y = point.y - POINT_WIDTH / 2;

                graphics2D.fillOval(x, y, POINT_WIDTH, POINT_WIDTH);
            }
        }
    }

    private int getMaxValue(ArrayList<Long> values) {
        return values.stream().mapToInt(Long::intValue).max().orElse(1);
    }
}