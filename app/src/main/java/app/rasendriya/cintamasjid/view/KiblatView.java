package app.rasendriya.cintamasjid.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import app.rasendriya.cintamasjid.R;

import static app.rasendriya.cintamasjid.R.color.background_color;

public class KiblatView extends View {

    private float busur;
    private int lebar = 240;
    private int tinggi = 240;

    private Paint penandaGambar;
    private Paint paintText;
    private Paint gambarLingkaran;

    private String northString;

    private int textHeight;

    /** Konstruktor */
    public KiblatView(Context context) {
        super(context);
        initKiblatView();
    }

    public KiblatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        initKiblatView();
    }

    public KiblatView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initKiblatView();
    }

    protected void initKiblatView() {
        setFocusable(true);

        gambarLingkaran = new Paint(Paint.ANTI_ALIAS_FLAG);
        gambarLingkaran.setColor(background_color);
        gambarLingkaran.setStrokeWidth(1);
        gambarLingkaran.setStyle(Paint.Style.FILL_AND_STROKE);

        Resources r = this.getResources();
        northString = r.getString(R.string.cardinal_north);

        paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(r.getColor(R.color.text_color));

        textHeight = (int) paintText.measureText("yY");

        penandaGambar = new Paint(Paint.ANTI_ALIAS_FLAG);
        penandaGambar.setColor(r.getColor(R.color.marker_color));
        penandaGambar.setFakeBoldText(true);
        penandaGambar.setStrokeWidth(4);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // The compass is a circle that fills as much space as possible.
        // Set the measured dimensions by figuring out the shortest boundary,
        // height or width.
        // int measuredWidth = measure(widthMeasureSpec);
        // int measuredHeight = measure(heightMeasureSpec);
        // int d = Math.min(measuredWidth, measuredHeight);

        setMeasuredDimension(lebar, tinggi);
    }

	/*
	 * private int measure(int measureSpec) { int result = 0;
	 * 
	 * // Decode the measurement specifications. int specMode =
	 * MeasureSpec.getMode(measureSpec); int specSize =
	 * MeasureSpec.getSize(measureSpec);
	 * 
	 * if (specMode == MeasureSpec.UNSPECIFIED) { // Return a default size of
	 * 200 if no bounds are specified. result = 200; } else { // As you want to
	 * fill the available space // always return the full available bounds.
	 * result = specSize; } return result; }
	 */
    /** untuk mengatur arah */
    public void setBearing(float _bearing) {
        busur = _bearing;

    }

    /** mengambil arah */
    public float getBusur() {
        return busur;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int px = getMeasuredWidth() / 2;
        int py = getMeasuredHeight() / 2;
        int radius = Math.min(px, py);

        // Draw the background
        canvas.drawCircle(px, py, radius, gambarLingkaran);
        // Rotate our perspective so that the 'top' is
        // facing the current bearing.
        canvas.save();
        canvas.rotate(-busur, px, py);
        int textWidth = (int) paintText.measureText("W");
        int cardinalX = px - textWidth / 2;
        int cardinalY = py - radius + textHeight;
        // canvas.drawLine(px, py, cardinalX, cardinalY, markerPaint);
        // canvas.save();
        // canvas.rotate(-(bearing+295), px, py);
        // Draw the marker every 15 degrees and a text every 45.
        for (int i = 0; i < 24; i++) {
            // Draw a marker.
            // canvas.drawLine(px, py-radius, px, py-radius+10, markerPaint);

            canvas.save();
            canvas.translate(0, textHeight);

            // Draw the cardinal points
			/*
			 * if (i % getKiblat() == 0) { String dirString = "KB"; int arrowY =
			 * 2*textHeight;
			 * 
			 * canvas.drawLine(px, arrowY, px-5, 3*textHeight, markerPaint);
			 * canvas.drawLine(px, arrowY, px+5, 3*textHeight, markerPaint);
			 * canvas.drawText(dirString, cardinalX, cardinalY, textPaint); }
			 */
            if (i / 6 == 0) {
                String dirString = "";
                switch (i) {
                    case (0): {
                        dirString = northString;
                        int arrowY = 2 * textHeight;
                        canvas.drawLine(px, arrowY, px - 5, 3 * textHeight,
                                penandaGambar);
                        canvas.drawLine(px, arrowY, px + 5, 3 * textHeight,
                                penandaGambar);
                        canvas.drawLine(px, py, px, arrowY, penandaGambar);
                        break;
                    }
                    // case(6) : dirString = eastString; break;
                    // case(12) : dirString = southString; break;
                    // case(18) : dirString = westString; break;
                }
                // canvas.drawText(dirString, cardinalX, cardinalY, paintText);
            } else if (i % 3 == 0) {
                // Draw the text every alternate 45deg
                // String angle = String.valueOf(i*15);
                // float angleTextWidth = textPaint.measureText(angle);
                //
                // int angleTextX = (int)(px-angleTextWidth/2);
                // int angleTextY = py-radius+textHeight;
                // canvas.drawText(angle, angleTextX, angleTextY, textPaint);
            }

            canvas.restore();

            canvas.rotate(15, px, py);
        }
        canvas.restore();
    }

}