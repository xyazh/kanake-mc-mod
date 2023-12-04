package com.xyazh.kanake.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ResourceLocationHelper {
    public static String loadResourceToStr(ResourceLocation resourceLocation) {
        IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
        try {
            IResource resource = resourceManager.getResource(resourceLocation);
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            StringBuilder shaderSource = new StringBuilder();
            String line = null;
            while (true) {
                try {
                    if ((line = reader.readLine()) == null) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                shaderSource.append(line).append('\n');
            }
            reader.close();
            return shaderSource.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
