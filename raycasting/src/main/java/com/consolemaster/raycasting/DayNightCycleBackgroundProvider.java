package com.consolemaster.raycasting;

import com.consolemaster.StyledChar;
import com.consolemaster.AnsiColor;
import com.consolemaster.AnimationTicker;
import lombok.Getter;
import lombok.Setter;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;

/**
 * A background provider that simulates a complete day-night cycle with weather systems.
 * Features automatic weather changes, sun/moon movement, and realistic sky colors.
 * Includes sunny, cloudy, rainy, and stormy weather conditions.
 * Note: This provider must be manually registered with the AnimationManager for animation.
 */
@Getter
@Setter
public class DayNightCycleBackgroundProvider implements BackgroundProvider, AnimationTicker {

    // Time and speed settings
    private double timeSpeed = 0.00001; // How fast time progresses (0.01 = slow, 0.1 = fast)
    private double currentTime = 0.5; // Time of day (0.0 = midnight, 0.5 = noon, 1.0 = midnight)

    // Weather settings
    private boolean automaticWeather = false;
    private double weatherChangeSpeed = 0.001; // How often weather changes
    private double sunnyWeatherBias = 0.99; // Probability bias towards sunny weather (0.0 = equal, 1.0 = always sunny)
    private WeatherType currentWeather = WeatherType.SUNNY;
    private double weatherTransition = 0.0; // Current weather transition progress (0.0 to 1.0)
    private WeatherType targetWeather = WeatherType.SUNNY;

    // Visual settings
    private boolean showCelestialBodies = true; // Show sun, moon, stars
    private double rainIntensity = 1.0;
    private double stormIntensity = 1.0;

    private Random random = new Random();
    private int canvasWidth = 80;
    private int canvasHeight = 25;
    private double playerAngle = 0.0;

    // Celestial objects
    private List<Star> stars = new ArrayList<>();
    private double weatherTimer = 0.0;

    public enum WeatherType {
        SUNNY("Sunny", AnsiColor.CYAN, AnsiColor.BRIGHT_CYAN),
        CLOUDY("Cloudy", AnsiColor.BRIGHT_BLACK, AnsiColor.WHITE),
        RAINY("Rainy", AnsiColor.BLUE, AnsiColor.BRIGHT_BLUE),
        STORMY("Stormy", AnsiColor.BLACK, AnsiColor.BRIGHT_BLACK);

        private final String name;
        private final AnsiColor primaryColor;
        private final AnsiColor secondaryColor;

        WeatherType(String name, AnsiColor primaryColor, AnsiColor secondaryColor) {
            this.name = name;
            this.primaryColor = primaryColor;
            this.secondaryColor = secondaryColor;
        }

        public String getName() { return name; }
        public AnsiColor getPrimaryColor() { return primaryColor; }
        public AnsiColor getSecondaryColor() { return secondaryColor; }
    }

    public DayNightCycleBackgroundProvider() {
        initializeStars();
    }

    public DayNightCycleBackgroundProvider(int canvasWidth, int canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        initializeStars();
    }

    private void initializeStars() {
        stars.clear();
        // Create stars for nighttime
        for (int i = 0; i < 50; i++) {
            double x = random.nextDouble() * canvasWidth;
            double y = random.nextDouble() * (canvasHeight * 0.7); // Only in upper part of sky
            double brightness = 0.3 + random.nextDouble() * 0.7;
            double twinklePhase = random.nextDouble() * Math.PI * 2;
            double twinkleSpeed = 0.02 + random.nextDouble() * 0.08;

            stars.add(new Star(x, y, brightness, twinklePhase, twinkleSpeed));
        }
    }

    @Override
    public void setDimensionAndAngle(int width, int height, double playerAngle) {
        this.playerAngle = playerAngle;
        if (width != this.canvasWidth || height != this.canvasHeight) {
            this.canvasWidth = width;
            this.canvasHeight = height;
            initializeStars();
        }
    }

    @Override
    public StyledChar getBackground(int x, int y) {
        // Calculate sky color based on time of day and weather
        AnsiColor skyColor = calculateSkyColor();

        // Check for weather effects first (they can hide celestial bodies)
        StyledChar weather = getWeatherEffect(x, y, skyColor);
        if (weather != null) {
            return weather;
        }

        // Check for celestial bodies (sun, moon, stars) - only if not covered by weather
        if (showCelestialBodies) {
            StyledChar celestial = getCelestialBody(x, y);
            if (celestial != null) {
                return celestial;
            }
        }


        // Return sky
        return new StyledChar(' ', null, skyColor, null);
    }

    private AnsiColor calculateSkyColor() {
        // Calculate base sky color from time of day
        AnsiColor timeColor;

        if (isNight()) {
            timeColor = AnsiColor.BLACK;
        } else if (isDawn() || isDusk()) {
            // Sunrise/sunset colors
            double dawnDuskProgress = getDawnDuskProgress();
            if (dawnDuskProgress < 0.5) {
                timeColor = AnsiColor.MAGENTA; // Purple dawn/dusk
            } else {
                timeColor = AnsiColor.YELLOW; // Orange dawn/dusk
            }
        } else {
            // Daytime
            timeColor = AnsiColor.CYAN;
        }

        // Modify color based on weather
        return modifyColorForWeather(timeColor);
    }

    private AnsiColor modifyColorForWeather(AnsiColor baseColor) {
        // Blend current weather with target weather based on transition
        WeatherType effectiveWeather = currentWeather;

        if (weatherTransition > 0.5 && targetWeather != currentWeather) {
            effectiveWeather = targetWeather;
        }

        switch (effectiveWeather) {
            case CLOUDY:
                return baseColor == AnsiColor.CYAN ? AnsiColor.BRIGHT_BLACK : baseColor;
            case RAINY:
                return baseColor == AnsiColor.CYAN ? AnsiColor.BLUE :
                       baseColor == AnsiColor.BLACK ? AnsiColor.BLUE : baseColor;
            case STORMY:
                return AnsiColor.BLACK;
            default:
                return baseColor;
        }
    }

    private StyledChar getCelestialBody(int x, int y) {
        double adjustedX = (x + (playerAngle / (Math.PI * 2)) * canvasWidth) % canvasWidth;

        if (isDay()) {
            // Draw sun
            StyledChar sun = getSun(adjustedX, y);
            if (sun != null) return sun;
        } else if (isNight()) {
            // Draw moon
            StyledChar moon = getMoon(adjustedX, y);
            if (moon != null) return moon;

            // Draw stars
            StyledChar star = getStars(adjustedX, y);
            if (star != null) return star;
        }

        return null;
    }

    private StyledChar getSun(double x, double y) {
        // Calculate sun position based on time
        double sunProgress = (currentTime - 0.25) / 0.5; // Sun visible from 6am to 6pm
        if (sunProgress < 0 || sunProgress > 1) return null;

        double sunX = sunProgress * canvasWidth;
        double sunY = Math.sin(sunProgress * Math.PI) * (canvasHeight * 0.3) + (canvasHeight * 0.1);

        double distance = Math.sqrt(Math.pow(x - sunX, 2) + Math.pow(y - sunY, 2));

        // Larger, more visible sun with extended radius and multiple layers
        if (distance < 3.0) { // Increased radius from 1.5 to 3.0
            if (distance < 0.8) {
                return new StyledChar('☀', AnsiColor.BRIGHT_YELLOW, calculateSkyColor(), null);
            } else if (distance < 1.5) {
                return new StyledChar('*', AnsiColor.BRIGHT_YELLOW, calculateSkyColor(), null);
            } else if (distance < 2.2) {
                return new StyledChar('*', AnsiColor.YELLOW, calculateSkyColor(), null);
            } else {
                return new StyledChar('·', AnsiColor.BRIGHT_YELLOW, calculateSkyColor(), null);
            }
        }

        return null;
    }

    private StyledChar getMoon(double x, double y) {
        // Calculate moon position (opposite to sun)
        double moonProgress = currentTime < 0.5 ? (currentTime + 0.5) : (currentTime - 0.5);
        moonProgress = (moonProgress - 0.25) / 0.5;
        if (moonProgress < 0 || moonProgress > 1) return null;

        double moonX = moonProgress * canvasWidth;
        double moonY = Math.sin(moonProgress * Math.PI) * (canvasHeight * 0.3) + (canvasHeight * 0.1);

        double distance = Math.sqrt(Math.pow(x - moonX, 2) + Math.pow(y - moonY, 2));

        // Larger, more visible moon with extended radius and multiple layers
        if (distance < 2.5) { // Increased radius from 1.0 to 2.5
            if (distance < 0.8) {
                return new StyledChar('☽', AnsiColor.BRIGHT_WHITE, calculateSkyColor(), null);
            } else if (distance < 1.5) {
                return new StyledChar('○', AnsiColor.WHITE, calculateSkyColor(), null);
            } else if (distance < 2.0) {
                return new StyledChar('○', AnsiColor.BRIGHT_BLACK, calculateSkyColor(), null);
            } else {
                return new StyledChar('·', AnsiColor.WHITE, calculateSkyColor(), null);
            }
        }

        return null;
    }

    private StyledChar getStars(double x, double y) {
        // Only show stars at night and if weather allows
        if (!isNight() || currentWeather == WeatherType.STORMY) return null;

        double starVisibility = getStarVisibility();
        if (starVisibility < 0.3) return null;

        for (Star star : stars) {
            double adjustedStarX = (star.x + (playerAngle / (Math.PI * 2)) * canvasWidth) % canvasWidth;
            if (adjustedStarX < 0) adjustedStarX += canvasWidth;

            if (Math.abs(adjustedStarX - x) < 1.0 && Math.abs(star.y - y) < 1.0) {
                double twinkle = Math.sin(star.twinklePhase) * 0.3 + 0.7;
                double effectiveBrightness = star.brightness * twinkle * starVisibility;

                if (effectiveBrightness > 0.5) {
                    char starChar = effectiveBrightness > 0.8 ? '*' : '·';
                    AnsiColor starColor = effectiveBrightness > 0.7 ? AnsiColor.WHITE : AnsiColor.BRIGHT_BLACK;
                    return new StyledChar(starChar, starColor, calculateSkyColor(), null);
                }
            }
        }

        return null;
    }

    private StyledChar getWeatherEffect(int x, int y, AnsiColor skyColor) {
        switch (currentWeather) {
            case CLOUDY:
                return getCloudEffect(x, y, skyColor);
            case RAINY:
                return getRainEffect(x, y, skyColor);
            case STORMY:
                return getStormEffect(x, y, skyColor);
            default:
                return null;
        }
    }

    private StyledChar getCloudEffect(int x, int y, AnsiColor skyColor) {
        // Simple cloud simulation
        double cloudNoise = getPerlinNoise(x * 0.1, y * 0.2 + weatherTimer * 0.1);
        if (cloudNoise > 0.6) {
            if (cloudNoise > 0.8) {
                return new StyledChar('█', AnsiColor.WHITE, skyColor, null);
            } else if (cloudNoise > 0.7) {
                return new StyledChar('▓', AnsiColor.BRIGHT_BLACK, skyColor, null);
            } else {
                return new StyledChar('░', AnsiColor.BRIGHT_BLACK, skyColor, null);
            }
        }
        return null;
    }

    private StyledChar getRainEffect(int x, int y, AnsiColor skyColor) {
        // Rain drops
        double rainNoise = getPerlinNoise(x * 0.3, y * 0.1 + weatherTimer * 2.0);
        if (rainNoise > 0.7 * (2.0 - rainIntensity)) {
            char rainChar = rainIntensity > 0.7 ? '|' : '\'';

            // Adjust rain color based on time of day
            AnsiColor rainColor;
            if (isNight()) {
                // Darker rain at night
                rainColor = AnsiColor.BLACK;
            } else if (isDawn() || isDusk()) {
                // Medium rain at dawn/dusk
                rainColor = AnsiColor.BRIGHT_BLACK;
            } else {
                // Bright rain during day
                rainColor = AnsiColor.BLUE;
            }

            return new StyledChar(rainChar, rainColor, skyColor, null);
        }

        // Background clouds
        StyledChar cloud = getCloudEffect(x, y, skyColor);
        if (cloud != null) return cloud;

        return null;
    }

    private StyledChar getStormEffect(int x, int y, AnsiColor skyColor) {
        // Lightning effect (random flashes)
        if (random.nextDouble() < 0.001 * stormIntensity) {
            return new StyledChar('⚡', AnsiColor.BRIGHT_YELLOW, AnsiColor.BLACK, null);
        }

        // Heavy rain with time-based coloring
        double stormNoise = getPerlinNoise(x * 0.5, y * 0.1 + weatherTimer * 3.0);
        if (stormNoise > 0.5) {
            // Adjust storm rain color based on time of day
            AnsiColor stormRainColor;
            if (isNight()) {
                // Very dark storm rain at night
                stormRainColor = AnsiColor.BLACK;
            } else if (isDawn() || isDusk()) {
                // Dark storm rain at dawn/dusk
                stormRainColor = AnsiColor.BRIGHT_BLACK;
            } else {
                // Bright storm rain during day
                stormRainColor = AnsiColor.BRIGHT_BLUE;
            }

            return new StyledChar('║', stormRainColor, skyColor, null);
        }

        // Dark storm clouds
        double cloudNoise = getPerlinNoise(x * 0.05, y * 0.1 + weatherTimer * 0.05);
        if (cloudNoise > 0.4) {
            return new StyledChar('█', AnsiColor.BLACK, skyColor, null);
        }

        return null;
    }

    private double getPerlinNoise(double x, double y) {
        // Simple pseudo-Perlin noise
        return (Math.sin(x) * Math.cos(y) + Math.sin(x * 2.1) * Math.cos(y * 1.7) +
                Math.sin(x * 0.7) * Math.cos(y * 2.3)) / 3.0 + 0.5;
    }

    private boolean isDay() {
        return currentTime > 0.25 && currentTime < 0.75;
    }

    private boolean isNight() {
        return currentTime < 0.25 || currentTime > 0.75;
    }

    private boolean isDawn() {
        return currentTime > 0.2 && currentTime < 0.3;
    }

    private boolean isDusk() {
        return currentTime > 0.7 && currentTime < 0.8;
    }

    private double getDawnDuskProgress() {
        if (isDawn()) {
            return (currentTime - 0.2) / 0.1;
        } else if (isDusk()) {
            return (currentTime - 0.7) / 0.1;
        }
        return 0.0;
    }

    private double getStarVisibility() {
        if (currentWeather == WeatherType.STORMY) return 0.0;
        if (currentWeather == WeatherType.RAINY) return 0.2;
        if (currentWeather == WeatherType.CLOUDY) return 0.6;
        return 1.0;
    }

    @Override
    public boolean tick() {
        // Update time
        currentTime += timeSpeed;
        if (currentTime >= 1.0) {
            currentTime -= 1.0;
        }

        // Update weather timer
        weatherTimer += 0.1;

        // Handle automatic weather changes
        if (automaticWeather) {
            updateAutomaticWeather();
        }

        // Update weather transition
        if (currentWeather != targetWeather) {
            weatherTransition += 0.02; // Transition speed
            if (weatherTransition >= 1.0) {
                currentWeather = targetWeather;
                weatherTransition = 0.0;
            }
        }

        // Update star twinkling
        for (Star star : stars) {
            star.twinklePhase += star.twinkleSpeed;
        }

        return true;
    }

    private void updateAutomaticWeather() {
        // Change weather randomly but slowly, with bias towards sunny weather
        if (random.nextDouble() < weatherChangeSpeed) {
            WeatherType newWeather = selectWeatherWithBias();

            // Don't change to the same weather
            if (newWeather != currentWeather) {
                setTargetWeather(newWeather);
            }
        }
    }

    /**
     * Selects a new weather type with bias towards sunny weather.
     * The sunnyWeatherBias parameter controls how likely sunny weather is:
     * - 0.0 = equal probability for all weather types
     * - 0.5 = sunny weather is moderately favored
     * - 0.7 = sunny weather is strongly favored (default)
     * - 1.0 = always sunny weather
     */
    private WeatherType selectWeatherWithBias() {
        double rand = random.nextDouble();

        // If random value is below the sunny bias, return sunny weather
        if (rand < sunnyWeatherBias) {
            return WeatherType.SUNNY;
        }

        // Otherwise, select from remaining weather types
        // Scale the remaining probability space (1.0 - sunnyWeatherBias) across other weather types
        double remainingSpace = 1.0 - sunnyWeatherBias;
        double scaledRand = (rand - sunnyWeatherBias) / remainingSpace;

        // Distribute remaining probability equally among non-sunny weather types
        WeatherType[] nonSunnyWeathers = {WeatherType.CLOUDY, WeatherType.RAINY, WeatherType.STORMY};
        int index = (int) (scaledRand * nonSunnyWeathers.length);
        index = Math.min(index, nonSunnyWeathers.length - 1); // Ensure we don't go out of bounds

        return nonSunnyWeathers[index];
    }
    // Public methods for manual weather control
    public void setWeather(WeatherType weather) {
        this.automaticWeather = false;
        setTargetWeather(weather);
    }

    public void setTargetWeather(WeatherType weather) {
        if (this.targetWeather != weather) {
            this.targetWeather = weather;
            this.weatherTransition = 0.0;
        }
    }

    public void setTimeOfDay(double time) {
        this.currentTime = Math.max(0.0, Math.min(1.0, time));
    }

    public String getTimeString() {
        int hours = (int) (currentTime * 24);
        int minutes = (int) ((currentTime * 24 - hours) * 60);
        return String.format("%02d:%02d", hours, minutes);
    }

    public String getCurrentWeatherString() {
        if (currentWeather != targetWeather) {
            return currentWeather.getName() + " → " + targetWeather.getName();
        }
        return currentWeather.getName();
    }

    /**
     * Star class for nighttime sky
     */
    private static class Star {
        double x, y;
        double brightness;
        double twinklePhase;
        double twinkleSpeed;

        Star(double x, double y, double brightness, double twinklePhase, double twinkleSpeed) {
            this.x = x;
            this.y = y;
            this.brightness = brightness;
            this.twinklePhase = twinklePhase;
            this.twinkleSpeed = twinkleSpeed;
        }
    }
}
