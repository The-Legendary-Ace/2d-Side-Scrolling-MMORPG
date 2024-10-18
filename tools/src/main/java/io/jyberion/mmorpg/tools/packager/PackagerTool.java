package io.jyberion.mmorpg.tools.packager;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.io.File;
import java.util.ArrayList;

public class PackagerTool extends ApplicationAdapter {

    private Stage stage;
    private Skin skin;
    private TextField sourceFolderField;
    private TextField outputFolderField;
    private TextArea logArea;

    @Override
    public void create() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Create UI elements
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label titleLabel = new Label("Asset Packager Tool", skin);
        Label sourceFolderLabel = new Label("Source Folder:", skin);
        sourceFolderField = new TextField("", skin);
        Label outputFolderLabel = new Label("Output Folder:", skin);
        outputFolderField = new TextField("", skin);

        TextButton browseSourceButton = new TextButton("Browse", skin);
        TextButton browseOutputButton = new TextButton("Browse", skin);
        TextButton packageButton = new TextButton("Package Assets", skin);
        logArea = new TextArea("", skin);
        logArea.setDisabled(true);

        // Arrange UI elements
        table.add(titleLabel).colspan(3).padBottom(20).row();
        table.add(sourceFolderLabel).left();
        table.add(sourceFolderField).width(400);
        table.add(browseSourceButton).width(100).row();
        table.add(outputFolderLabel).left();
        table.add(outputFolderField).width(400);
        table.add(browseOutputButton).width(100).row();
        table.add(packageButton).colspan(3).padTop(20).row();
        table.add(logArea).colspan(3).expand().fill();

        // Add listeners (implement file browsing and packaging logic)
        // For the purposes of this example, we'll keep it simple

        packageButton.addListener(event -> {
            if (packageButton.isPressed()) {
                packageAssets();
                return true;
            }
            return false;
        });

        Gdx.input.setInputProcessor(stage);
    }

    private void packageAssets() {
        String sourceFolder = sourceFolderField.getText();
        String outputFolder = outputFolderField.getText();

        if (sourceFolder.isEmpty() || outputFolder.isEmpty()) {
            log("Please specify both source and output folders.");
            return;
        }

        log("Packaging assets...");

        // Implement packaging logic here
        // For each subfolder in sourceFolder, create a .sc12 file with encryption
        File sourceDir = new File(sourceFolder);
        File outputDir = new File(outputFolder);

        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            log("Invalid source folder.");
            return;
        }

        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        File[] subFolders = sourceDir.listFiles(File::isDirectory);
        if (subFolders != null) {
            for (File folder : subFolders) {
                packageFolder(folder, outputDir);
            }
        }

        log("Packaging completed.");
    }

    private void packageFolder(File folder, File outputDir) {
        try {
            String folderName = folder.getName();
            File outputFile = new File(outputDir, folderName + ".sc12");

            // Collect all files in the folder
            ArrayList<File> files = new ArrayList<>();
            collectFiles(folder, files);

            // Package files into .sc12 with encryption
            AssetPacker.packAssets(folder, files, outputFile);

            log("Packaged " + folderName + " into " + outputFile.getName());
        } catch (Exception e) {
            log("Error packaging folder " + folder.getName() + ": " + e.getMessage());
        }
    }

    private void collectFiles(File dir, ArrayList<File> fileList) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    fileList.add(file);
                } else if (file.isDirectory()) {
                    collectFiles(file, fileList);
                }
            }
        }
    }

    private void log(String message) {
        logArea.appendText(message + "\n");
    }

    @Override
    public void render() {
        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
