package com.xyazh.kanake.render;

import java.nio.FloatBuffer;

import com.xyazh.kanake.libs.weaponlib.shader.jim.Shader;
import com.xyazh.kanake.libs.weaponlib.shader.jim.ShaderManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class InstancedRender {
	protected final float[] VERTICES = {
			-0.5f, -0.5f, 0.0f,  // 左下角
			0.5f, -0.5f, 0.0f,   // 右下角
			0.0f, 0.5f, 0.0f     // 顶部
	};
	private static final Shader shad = ShaderManager.loadVMWShader("instanced");
	public final int vao;

	public InstancedRender() {
		this.vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(this.vao);
		// 创建并绑定 VBO
		int vbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(VERTICES.length);
		vertexBuffer.put(VERTICES).flip();
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);
		// 设置顶点属性指针
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
	}

	public void prepare() {
		shad.use();
		GL30.glBindVertexArray(this.vao);
		GL20.glEnableVertexAttribArray(0);
	}

	public void render() {
		prepare();
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);
		finishRender();
	}

	public void finishRender() {
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shad.release();
	}
}
