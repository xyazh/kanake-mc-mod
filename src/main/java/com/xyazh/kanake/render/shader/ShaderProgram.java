package com.xyazh.kanake.render.shader;

import com.xyazh.kanake.Kanake;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

public class ShaderProgram {
    public final int shaderProgram;
    public boolean isLinked = false;
    public ShaderProgram() {
        this.shaderProgram = GL20.glCreateProgram();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        GL20.glDeleteProgram(this.shaderProgram);
    }

    public void addShader(Shader shader) {
        if(this.isLinked){
            return;
        }
        GL20.glAttachShader(this.shaderProgram, shader.id);
    }

    public void link() {
        if(this.isLinked){
            return;
        }
        GL20.glLinkProgram(shaderProgram);
        int success = GL20.glGetProgrami(shaderProgram, GL20.GL_LINK_STATUS);
        if (success != 1) {
            String infoLog = GL20.glGetProgramInfoLog(shaderProgram,1024);
            Kanake.logger.error("ERROR::SHADER::PROGRAM::LINKING_FAILED\n{}",infoLog);
            return;
        }
        this.isLinked= true;
    }

    public void use() {
        GL20.glUseProgram(shaderProgram);
    }

    public void unUse() {
        GL20.glUseProgram(0);
    }
}
