package beanvest.scripts.tsgen;

import beanvest.processor.processingv2.dto.PortfolioStatsDto2;
import cz.habarta.typescript.generator.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class GenerateTypescriptTypesMain {

    public static void main(String[] args) throws IOException {
        String projectDir = System.getProperty("project.dir");

        TypeScriptGenerator typeScriptGenerator = createConfiguredGenerator();
        String generated = typeScriptGenerator.generateTypeScript(Input.from(PortfolioStatsDto2.class));
        String sanitized = removeNonDeterministicDetails(generated);

        Files.writeString(Path.of(projectDir + "/generated/apiTypes.d.ts"), sanitized);
    }

    private static String removeNonDeterministicDetails(String generated) {
        return generated.replaceFirst(" on \\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d\\.", "");
    }

    @NotNull
    private static TypeScriptGenerator createConfiguredGenerator() {
        Settings settings = new Settings();
        settings.outputFileType = TypeScriptFileType.implementationFile;
        settings.mapClasses = ClassMapping.asClasses;
        settings.outputKind = TypeScriptOutputKind.module;
        settings.jsonLibrary = JsonLibrary.gson;

        return new TypeScriptGenerator(settings);
    }
}