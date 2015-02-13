package org.teavm.samples.benchmark.defrac;

import com.defrac.benchmark.BenchmarkResults;
import defrac.app.Bootstrap;
import defrac.app.GenericApp;
import defrac.display.Canvas;
import defrac.display.graphics.Graphics;
import defrac.util.Color;
import defrac.util.Timer;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.teavm.samples.benchmark.Scene;

/**
 *
 */
public final class BenchmarkStarter extends GenericApp {
  private static Scene scene = new Scene();
  private static int currentSecond;
  private static long startMillisecond;
  private static double timeSpentCalculating;
  private static BenchmarkResults results = new BenchmarkResults();

  private Canvas canvas;
  private Graphics graphics;

  private BenchmarkStarter() {
  }

  public static void main(String[] args) {
    Bootstrap.run(new BenchmarkStarter());
  }

  @Override
  protected void onStart() {
    graphics = addChild(canvas = new Canvas(600, 600)).graphics();
    canvas.centerRegistrationPoint();
    onResize(width(), height());
    startMillisecond = System.currentTimeMillis();
    makeStep();
  }

  @Override
  protected void onResize(final float width, final float height) {
    canvas.moveTo(width * 0.5f, height * 0.5f);
  }

  private void makeStep() {
    double start = System.currentTimeMillis();
    scene.calculate();
    double end = System.currentTimeMillis();
    int second = (int)((System.currentTimeMillis() - startMillisecond) / 1000);
    if (second > currentSecond) {
      if(second > 10) {
        results.add(timeSpentCalculating);
      }
      System.out.println(String.format("second: %d, time: %3.2f (best: %3.2f, mean: %3.2f, error: +-%2.2f%%",
          second, timeSpentCalculating, results.bestMs(), results.meanMs(), results.errorPercent()));
      timeSpentCalculating = 0;
      currentSecond = second;
    }

    timeSpentCalculating += end - start;
    render();

    new Timer(scene.timeUntilNextStep(), 1).listener(
        new Timer.SimpleListener() {
          @Override
          public void onTimerComplete(final Timer timer) {
            makeStep();
          }
        }
    ).start();
  }

  private void render() {
    graphics.
        strokeStyle(Color.GRAY).
        clearRect(0, 0, 600, 600).
        save().
        translate(0, 600).
        scale(1, -1).
        scale(100, 100).
        lineWidth(0.01f);
    for (Body body = scene.getWorld().getBodyList(); body != null; body = body.getNext()) {
      Vec2 center = body.getPosition();
      graphics.
          save().
          translate(center.x, center.y).
          rotate(body.getAngle());
      for (Fixture fixture = body.getFixtureList(); fixture != null; fixture = fixture.getNext()) {
        Shape shape = fixture.getShape();
        if (shape.getType() == ShapeType.CIRCLE) {
          CircleShape circle = (CircleShape)shape;
          graphics.
              beginPath().
              circle(circle.m_p.x, circle.m_p.y, circle.getRadius()).
              closePath()
              .stroke();
        } else if (shape.getType() == ShapeType.POLYGON) {
          PolygonShape poly = (PolygonShape)shape;
          Vec2[] vertices = poly.getVertices();
          graphics.beginPath().moveTo(vertices[0].x, vertices[0].y);
          for (int i = 1; i < poly.getVertexCount(); ++i) {
            graphics.lineTo(vertices[i].x, vertices[i].y);
          }
          graphics.closePath();
          graphics.stroke();
        }
      }
      graphics.restore();
    }
    graphics.restore();
  }
}
