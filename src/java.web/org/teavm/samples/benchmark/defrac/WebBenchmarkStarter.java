package org.teavm.samples.benchmark.defrac;

import com.defrac.benchmark.BenchmarkResults;
import defrac.lang.Bridge;
import defrac.web.*;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.teavm.samples.benchmark.Scene;

import java.lang.Math;

/**
 *
 */
public final class WebBenchmarkStarter {
  private static Window window = Toplevel.window();
  private static HTMLDocument document = (HTMLDocument)window.document;
  private static HTMLCanvasElement canvas = (HTMLCanvasElement)document.getElementById("benchmark-canvas");
  private static CanvasRenderingContext2D context = (CanvasRenderingContext2D)canvas.getContext("2d");
  private static HTMLElement resultTableBody = (HTMLElement)document.getElementById("result-table-body");
  private static Performance performance = (Performance)Bridge.getUnsafeObject(window, "performance");
  private static HTMLElement resultBest = (HTMLElement)document.getElementById("result-best");
  private static HTMLElement resultMean = (HTMLElement)document.getElementById("result-mean");
  private static HTMLElement resultError = (HTMLElement)document.getElementById("result-error");
  private static Scene scene = new Scene();
  private static int currentSecond;
  private static long startMillisecond;
  private static double timeSpentCalculating;
  private static BenchmarkResults results = new BenchmarkResults();

  private WebBenchmarkStarter() {
  }

  public static void main(String[] args) {
    startMillisecond = System.currentTimeMillis();
    canvas.width = (int)(600.0f * window.devicePixelRatio + 0.5f);
    canvas.height = (int)(600.0f * window.devicePixelRatio + 0.5f);
    context.scale(window.devicePixelRatio, window.devicePixelRatio);
    makeStep();
  }

  private static void makeStep() {
    double start = performance.now();
    scene.calculate();
    double end = performance.now();
    int second = (int)((System.currentTimeMillis() - startMillisecond) / 1000);
    if (second > currentSecond) {
      Element row = document.createElement("tr");
      resultTableBody.appendChild(row);
      Element secondCell = document.createElement("td");
      row.appendChild(secondCell);
      secondCell.appendChild(document.createTextNode(String.valueOf(second)));
      Element timeCell = document.createElement("td");
      row.appendChild(timeCell);
      timeCell.appendChild(document.createTextNode(String.valueOf(timeSpentCalculating)));

      if(second > 10) {
        results.add(timeSpentCalculating);
        resultBest.textContent = String.valueOf(toFixed(results.bestMs()));
        resultMean.textContent = String.valueOf(toFixed(results.meanMs()));
        resultError.textContent = String.valueOf(toFixed(results.errorPercent()));
      }

      timeSpentCalculating = 0;
      currentSecond = second;
    }
    timeSpentCalculating += end - start;
    render();
    window.setTimeout(event -> {
      makeStep();
    }, scene.timeUntilNextStep());
  }

  private static float toFixed(double value) {
    return (Math.round((float)value * 100.0f) / 100.0f);
  }

  private static void render() {
    CanvasRenderingContext2D ctx = context;
    ctx.strokeStyleAsString = "grey";
    ctx.clearRect(0, 0, 600, 600);
    ctx.save();
    ctx.translate(0, 600);
    ctx.scale(1, -1);
    ctx.scale(100, 100);
    ctx.lineWidth = 0.01;
    for (Body body = scene.getWorld().getBodyList(); body != null; body = body.getNext()) {
      Vec2 center = body.getPosition();
      ctx.save();
      ctx.translate(center.x, center.y);
      ctx.rotate(body.getAngle());
      for (Fixture fixture = body.getFixtureList(); fixture != null; fixture = fixture.getNext()) {
        Shape shape = fixture.getShape();
        if (shape.getType() == ShapeType.CIRCLE) {
          CircleShape circle = (CircleShape)shape;
          ctx.beginPath();
          ctx.arc(circle.m_p.x, circle.m_p.y, circle.getRadius(), 0, java.lang.Math.PI * 2);
          ctx.closePath();
          ctx.stroke();
        } else if (shape.getType() == ShapeType.POLYGON) {
          PolygonShape poly = (PolygonShape)shape;
          Vec2[] vertices = poly.getVertices();
          ctx.beginPath();
          ctx.moveTo(vertices[0].x, vertices[0].y);
          for (int i = 1; i < poly.getVertexCount(); ++i) {
            ctx.lineTo(vertices[i].x, vertices[i].y);
          }
          ctx.closePath();
          ctx.stroke();
        }
      }
      ctx.restore();
    }
    ctx.restore();
  }
}
