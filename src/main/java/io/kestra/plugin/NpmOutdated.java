package io.kestra.plugin.npm;

import io.kestra.core.models.annotations.Plugin;
import io.kestra.core.models.tasks.Task;
import io.kestra.core.models.tasks.RunnableTask;
import io.kestra.core.runners.RunContext;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@SuperBuilder
@Getter
@NoArgsConstructor
@Plugin
public class NpmOutdated extends Task implements RunnableTask<NpmOutdated.Output> {

    // path to node project 
    private String path;

    // if true  return raw string output (table format)
    // if false/null  return json output
    private Boolean raw;

    // if true  run npm audit instead of npm outdated
    private Boolean audit;

    @Override
    public Output run(RunContext runContext) throws Exception {

        Map<String, Object> result = new HashMap<>();

        try {
            ProcessBuilder pb;

            // decide command based on audit  raw flags
            if (Boolean.TRUE.equals(audit)) {
                // run security audit 
                pb = new ProcessBuilder("npm", "audit", "--json");
            } else {
                if (Boolean.TRUE.equals(raw)) {
                    
                    pb = new ProcessBuilder("npm", "outdated");
                } else {
                    
                    pb = new ProcessBuilder("npm", "outdated", "--json");
                }
            }

            // set working directory if path is provided
            if (path != null) {
                pb.directory(new java.io.File(path));
            }

            Process process = pb.start();

            // read standard output
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );

            // read error output 
            BufferedReader errorReader = new BufferedReader(
                new InputStreamReader(process.getErrorStream())
            );

            StringBuilder output = new StringBuilder();
            StringBuilder errorOutput = new StringBuilder();

            String line;

            // collect stdout
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // collect stderr
            while ((line = errorReader.readLine()) != null) {
                errorOutput.append(line).append("\n");
            }

            int exitCode = process.waitFor();

            // if command failed, return error clearly
            if (exitCode != 0) {
                result.put("status", "failed");
                result.put("error", errorOutput.toString());
                return new Output(result);
            }

            // handle empty output 
            if (output.toString().trim().isEmpty()) {
                result.put("status", "success");
                result.put("message", "all packages are up to date");
                result.put("totalPackages", 0);
            } else {
                result.put("status", "success");

                if (Boolean.TRUE.equals(raw)) {
                    // return raw string output
                    result.put("format", "raw");
                    result.put("data", output.toString());
                } else {
                    // return json string directly 
                    result.put("format", "json");
                    result.put("data", output.toString());
                }
            }

        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
        }

        return new Output(result);
    }

    @Getter
    public static class Output implements io.kestra.core.models.tasks.Output {
        private final Map<String, Object> result;

        public Output(Map<String, Object> result) {
            this.result = result;
        }
    }
}