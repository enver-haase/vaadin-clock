/*
 * Copyright 2026 Infraleap
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.infraleap.vaadin.addon.clock;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.AbstractSinglePropertyField;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasAriaLabel;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasTheme;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.function.SerializableFunction;

/**
 * Clock is a component displaying an analog clock face with an Amiga Workbench
 * 1.2 aesthetic. The clock shows hour, minute, and second hands.
 * <p>
 * The clock has two modes controlled by {@link #setRunning(boolean)}:
 * <ul>
 * <li><b>Stopped (default):</b> Displays the time set via {@link #setValue(LocalTime)}</li>
 * <li><b>Running:</b> Time advances in real-time from the base value</li>
 * </ul>
 * <p>
 * When running, the clock tracks elapsed time using precise timestamps to avoid
 * rounding errors. The effective time is: base value + elapsed running time.
 * Setting a new value resets the accumulated running time.
 *
 * <h2>Styling</h2>
 * <p>
 * The clock can be styled using CSS custom properties:
 * <ul>
 * <li>{@code --vaadin-clock-size} - Size of the clock (default: 120px)</li>
 * <li>{@code --vaadin-clock-background} - Background color (default: #0055AA)</li>
 * <li>{@code --vaadin-clock-face-color} - Clock face color (default: #AAAAAA)</li>
 * <li>{@code --vaadin-clock-hour-hand-color} - Hour hand color (default: #000000)</li>
 * <li>{@code --vaadin-clock-minute-hand-color} - Minute hand color (default: #000000)</li>
 * <li>{@code --vaadin-clock-second-hand-color} - Second hand color (default: #FF8800)</li>
 * </ul>
 *
 * <h2>Theme Variants</h2>
 * <p>
 * The clock supports theme variants that can be set via {@link #addThemeVariants(ClockVariant...)}:
 * <ul>
 * <li>{@link ClockVariant#LUMO} - Modern, clean Lumo appearance</li>
 * <li>{@link ClockVariant#AURA} - Refined, professional Aura appearance</li>
 * <li>{@link ClockVariant#DARK} - Dark mode (combine with LUMO or AURA)</li>
 * </ul>
 *
 * @author Infraleap
 */
@Tag("vaadin-clock")
@JsModule("./vaadin-clock/vaadin-clock.js")
public class Clock extends AbstractSinglePropertyField<Clock, LocalTime>
        implements Focusable<Clock>, HasAriaLabel, HasSize, HasStyle, HasTheme {

    private static final SerializableFunction<String, LocalTime> PARSER = valueFromClient -> {
        return valueFromClient == null || valueFromClient.isEmpty() ? null
                : LocalTime.parse(valueFromClient);
    };

    private static final SerializableFunction<LocalTime, String> FORMATTER = valueFromModel -> {
        return valueFromModel == null ? "" : valueFromModel.toString();
    };

    private LocalTime min;
    private LocalTime max;
    private Duration step;

    /**
     * Default constructor. Creates a clock that displays the current time.
     */
    public Clock() {
        this((LocalTime) null);
    }

    /**
     * Creates a clock displaying the specified time.
     *
     * @param time
     *            the time to display, or {@code null} to display the current time
     */
    public Clock(LocalTime time) {
        super("value", null, String.class, PARSER, FORMATTER);

        if (time != null) {
            setValue(time);
        }
    }

    /**
     * Creates a clock with a value change listener.
     *
     * @param listener
     *            the value change listener
     */
    public Clock(
            ValueChangeListener<AbstractField.ComponentValueChangeEvent<Clock, LocalTime>> listener) {
        this();
        addValueChangeListener(listener);
    }

    /**
     * Creates a clock displaying the specified time with a value change
     * listener.
     *
     * @param time
     *            the time to display, or {@code null} to display the current time
     * @param listener
     *            the value change listener
     */
    public Clock(LocalTime time,
            ValueChangeListener<AbstractField.ComponentValueChangeEvent<Clock, LocalTime>> listener) {
        this(time);
        addValueChangeListener(listener);
    }

    /**
     * Sets the label for the clock.
     *
     * @param label
     *            value for the {@code label} property in the clock
     */
    public void setLabel(String label) {
        getElement().setProperty("label", label == null ? "" : label);
    }

    /**
     * Gets the label of the clock.
     *
     * @return the {@code label} property of the clock
     */
    public String getLabel() {
        return getElement().getProperty("label");
    }

    @Override
    public void setAriaLabel(String ariaLabel) {
        getElement().setProperty("accessibleName", ariaLabel);
    }

    @Override
    public Optional<String> getAriaLabel() {
        return Optional.ofNullable(getElement().getProperty("accessibleName"));
    }

    @Override
    public void setAriaLabelledBy(String labelledBy) {
        getElement().setProperty("accessibleNameRef", labelledBy);
    }

    @Override
    public Optional<String> getAriaLabelledBy() {
        return Optional
                .ofNullable(getElement().getProperty("accessibleNameRef"));
    }

    /**
     * Sets the displayed time. The value will be truncated to second precision.
     * <p>
     * Set to {@code null} to display the current time.
     *
     * @param value
     *            the time to display, or {@code null} for current time
     */
    @Override
    public void setValue(LocalTime value) {
        // Truncate to second precision
        if (value != null) {
            value = value.truncatedTo(ChronoUnit.SECONDS);
        }
        super.setValue(value);
    }

    /**
     * Gets the displayed time.
     *
     * @return the displayed time, or {@code null} if displaying current time
     */
    @Override
    public LocalTime getValue() {
        return super.getValue();
    }

    /**
     * Sets the minimum allowed time for user adjustment.
     *
     * @param min
     *            the minimum time, or {@code null} to remove the constraint
     */
    public void setMin(LocalTime min) {
        this.min = min;
        String minString = min != null ? min.toString() : "";
        getElement().setProperty("min", minString);
    }

    /**
     * Gets the minimum allowed time.
     *
     * @return the minimum time, or {@code null} if not set
     */
    public LocalTime getMin() {
        return this.min;
    }

    /**
     * Sets the maximum allowed time for user adjustment.
     *
     * @param max
     *            the maximum time, or {@code null} to remove the constraint
     */
    public void setMax(LocalTime max) {
        this.max = max;
        String maxString = max != null ? max.toString() : "";
        getElement().setProperty("max", maxString);
    }

    /**
     * Gets the maximum allowed time.
     *
     * @return the maximum time, or {@code null} if not set
     */
    public LocalTime getMax() {
        return this.max;
    }

    /**
     * Sets the clock to disabled state.
     *
     * @param disabled
     *            {@code true} to disable the clock, {@code false} to enable
     */
    public void setDisabled(boolean disabled) {
        getElement().setProperty("disabled", disabled);
    }

    /**
     * Gets whether the clock is disabled.
     *
     * @return {@code true} if disabled, {@code false} otherwise
     */
    public boolean isDisabled() {
        return getElement().getProperty("disabled", false);
    }

    /**
     * Sets the clock to readonly state.
     *
     * @param readonly
     *            {@code true} to set readonly, {@code false} otherwise
     */
    public void setReadonly(boolean readonly) {
        getElement().setProperty("readonly", readonly);
    }

    /**
     * Gets whether the clock is readonly.
     *
     * @return {@code true} if readonly, {@code false} otherwise
     */
    public boolean isReadonly() {
        return getElement().getProperty("readonly", false);
    }

    /**
     * Sets whether the clock time can be adjusted by the user via dragging
     * clock hands or using keyboard arrow keys.
     *
     * @param adjustable
     *            {@code true} to allow user adjustment (default),
     *            {@code false} to disable
     */
    public void setAdjustable(boolean adjustable) {
        getElement().setProperty("adjustable", adjustable);
    }

    /**
     * Gets whether the clock time can be adjusted by the user.
     *
     * @return {@code true} if user adjustment is enabled (default),
     *         {@code false} otherwise
     */
    public boolean isAdjustable() {
        return getElement().getProperty("adjustable", true);
    }

    /**
     * Sets the time interval for step-based adjustments when using keyboard
     * arrow keys.
     * <p>
     * The step must evenly divide a day. For example, "15 minutes", "30 minutes",
     * and "2 hours" are valid steps, but "42 minutes" is not valid.
     * <p>
     * Keyboard arrow keys adjust by at least 1 hour, using the smallest multiple
     * of the step that is at least 1 hour. For example, with a 10-minute step,
     * arrow keys adjust by 1 hour (6 × 10 minutes). With a 90-minute step,
     * arrow keys adjust by 90 minutes.
     * <p>
     * The default step is 60 seconds (1 minute).
     *
     * @param step
     *            the step duration, not {@code null}
     * @throws IllegalArgumentException
     *             if the step is not a positive duration or does not evenly
     *             divide a day
     */
    public void setStep(Duration step) {
        Objects.requireNonNull(step, "Step cannot be null");
        if (step.isNegative() || step.isZero()) {
            throw new IllegalArgumentException("Step must be a positive duration");
        }
        long stepSeconds = step.toSeconds();
        long daySeconds = 86400;
        if (daySeconds % stepSeconds != 0) {
            throw new IllegalArgumentException(
                    "Step must evenly divide a day");
        }
        this.step = step;
        getElement().setProperty("step", stepSeconds);
    }

    /**
     * Gets the time interval for step-based adjustments.
     *
     * @return the step duration, or {@code null} if not set (defaults to
     *         60 seconds on the client)
     */
    public Duration getStep() {
        return this.step;
    }

    /**
     * Sets the size of the clock using the CSS custom property.
     *
     * @param size
     *            the size (e.g., "150px", "10em")
     */
    public void setClockSize(String size) {
        Objects.requireNonNull(size, "Size cannot be null");
        getElement().getStyle().set("--vaadin-clock-size", size);
    }

    /**
     * Sets whether the clock is running (animating).
     * <p>
     * When running, the displayed time advances in real-time from the base
     * value set via {@link #setValue(LocalTime)}. The elapsed time is tracked
     * precisely using timestamps, so no rounding errors accumulate.
     * <p>
     * When the clock is stopped, the current displayed time (base time plus
     * accumulated running time) is synced back to the Java value, so
     * {@link #getValue()} returns the time that was shown when stopped.
     * <p>
     * The default is {@code false} (not running).
     *
     * @param running
     *            {@code true} to start running, {@code false} to stop
     */
    public void setRunning(boolean running) {
        boolean wasRunning = isRunning();
        getElement().setProperty("running", running);

        // When stopping a running clock, sync the current displayed time back to Java
        if (wasRunning && !running) {
            getElement().executeJs("return this.getValue()")
                    .then(String.class, currentValue -> {
                        if (currentValue != null && !currentValue.isEmpty()) {
                            LocalTime time = LocalTime.parse(currentValue);
                            // Update the model value without triggering client-side changes
                            setModelValue(time, false);
                        }
                    });
        }
    }

    /**
     * Returns whether the clock is currently running (animating).
     *
     * @return {@code true} if running, {@code false} otherwise
     */
    public boolean isRunning() {
        return getElement().getProperty("running", false);
    }

    /**
     * Adds theme variants to the component.
     *
     * @param variants
     *            the theme variants to add
     */
    public void addThemeVariants(ClockVariant... variants) {
        getThemeNames().addAll(
                Stream.of(variants).map(ClockVariant::getVariantName)
                        .collect(Collectors.toList()));
    }

    /**
     * Removes theme variants from the component.
     *
     * @param variants
     *            the theme variants to remove
     */
    public void removeThemeVariants(ClockVariant... variants) {
        getThemeNames().removeAll(
                Stream.of(variants).map(ClockVariant::getVariantName)
                        .collect(Collectors.toList()));
    }
}
