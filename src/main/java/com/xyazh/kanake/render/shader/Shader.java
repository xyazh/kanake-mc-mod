package com.xyazh.kanake.render.shader;

import com.xyazh.kanake.Kanake;
import org.lwjgl.opengl.GL20;

import javax.annotation.Nullable;

public class Shader {
    public final int id;

    @Nullable
    public static Shader craftVertexShaderFromString(String vertexShaderSource) {
        int vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(vertexShader, vertexShaderSource);
        GL20.glCompileShader(vertexShader);
        int success = GL20.glGetShaderi(vertexShader, GL20.GL_COMPILE_STATUS);
        if (success != 1) {
            String infoLog = GL20.glGetShaderInfoLog(vertexShader,1024);
            Kanake.logger.error("ERROR::SHADER::VERTEX::COMPILATION_FAILED\r\n{}",infoLog);
            return null;
        }
       return new Shader(vertexShader);
    }

    @Nullable
    public static Shader craftFragmentShaderFromString(String fragmentShaderSource) {
        int fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(fragmentShader, fragmentShaderSource);
        GL20.glCompileShader(fragmentShader);
        int success = GL20.glGetShaderi(fragmentShader, GL20.GL_COMPILE_STATUS);
        if (success != 1) {
            String infoLog = GL20.glGetShaderInfoLog(fragmentShader,1024);
            Kanake.logger.error("ERROR::SHADER::FRAGMENT::COMPILATION_FAILED\n{}",infoLog);
            return null;
        }
        return new Shader(fragmentShader);
    }

    protected Shader(int id){
        this.id = id;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        GL20.glDeleteShader(this.id);
    }
}
