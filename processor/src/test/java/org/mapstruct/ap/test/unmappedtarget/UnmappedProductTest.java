/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.unmappedtarget;

import static org.assertj.core.api.Assertions.assertThat;

import javax.tools.Diagnostic.Kind;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.testutil.IssueKey;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.compilation.annotation.CompilationResult;
import org.mapstruct.ap.testutil.compilation.annotation.Diagnostic;
import org.mapstruct.ap.testutil.compilation.annotation.ExpectedCompilationOutcome;
import org.mapstruct.ap.testutil.compilation.annotation.ProcessorOption;
import org.mapstruct.ap.testutil.runner.AnnotationProcessorTestRunner;

/**
 * Tests expected diagnostics for unmapped target properties.
 *
 * @author Gunnar Morling
 */
@IssueKey("35")
@RunWith(AnnotationProcessorTestRunner.class)
public class UnmappedProductTest {

    @Test
    @WithClasses({ Source.class, Target.class, SourceTargetMapper.class })
    @ExpectedCompilationOutcome(
        value = CompilationResult.SUCCEEDED,
        diagnostics = {
            @Diagnostic(type = SourceTargetMapper.class,
                kind = Kind.WARNING,
                line = 16,
                message = "Unmapped target property: \"bar\"."),
            @Diagnostic(type = SourceTargetMapper.class,
                kind = Kind.WARNING,
                line = 18,
                message = "Unmapped target property: \"qux\".")
        }
    )
    public void shouldLeaveUnmappedTargetPropertyUnset() {
        Source source = new Source();
        source.setFoo( 42L );

        Target target = SourceTargetMapper.INSTANCE.sourceToTarget( source );

        assertThat( target ).isNotNull();
        assertThat( target.getFoo() ).isEqualTo( 42L );
        assertThat( target.getBar() ).isEqualTo( 0 );
    }

    @Test
    @WithClasses({ Source.class, Target.class, ErroneousStrictSourceTargetMapper.class })
    @ExpectedCompilationOutcome(
        value = CompilationResult.FAILED,
        diagnostics = {
            @Diagnostic(type = ErroneousStrictSourceTargetMapper.class,
                kind = Kind.ERROR,
                line = 17,
                message = "Unmapped target property: \"bar\"."),
            @Diagnostic(type = ErroneousStrictSourceTargetMapper.class,
                kind = Kind.ERROR,
                line = 19,
                message = "Unmapped target property: \"qux\".")
        }
    )
    public void shouldRaiseErrorDueToUnsetTargetProperty() {
    }

    @Test
    @WithClasses({ Source.class, Target.class, SourceTargetMapper.class })
    @ProcessorOption(name = "mapstruct.unmappedTargetPolicy", value = "ERROR")
    @ExpectedCompilationOutcome(
        value = CompilationResult.FAILED,
        diagnostics = {
            @Diagnostic(type = SourceTargetMapper.class,
                kind = Kind.ERROR,
                line = 16,
                message = "Unmapped target property: \"bar\"."),
            @Diagnostic(type = SourceTargetMapper.class,
                kind = Kind.ERROR,
                line = 18,
                message = "Unmapped target property: \"qux\".")
        }
    )
    public void shouldRaiseErrorDueToUnsetTargetPropertyWithPolicySetViaProcessorOption() {
    }

    @Test
    @IssueKey("2132")
    @WithClasses({ Source.class, Target.class, ErroneousBeanMappingStrictSourceTargetMapper.class })
    @ExpectedCompilationOutcome(
            value = CompilationResult.FAILED,
            diagnostics = {
                    @Diagnostic(type = ErroneousBeanMappingStrictSourceTargetMapper.class,
                            kind = Kind.ERROR,
                            line = 20,
                            message = "Unmapped target property: \"bar\"."),
                    @Diagnostic(type = ErroneousBeanMappingStrictSourceTargetMapper.class,
                            kind = Kind.WARNING,
                            line = 22,
                            message = "Unmapped target property: \"qux\".")
            }
    )
    public void shouldRaiseErrorDueToUnsetTargetPropertyWithPolicySetViaBeanMapping() {
    }
}
