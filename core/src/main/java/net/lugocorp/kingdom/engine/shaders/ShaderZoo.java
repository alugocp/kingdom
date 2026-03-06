package net.lugocorp.kingdom.engine.shaders;

/**
 * This class bundles all the custom shaders
 */
public class ShaderZoo {
    public final ElementShader element = new ElementShader();
    public final OutlineShader outline = new OutlineShader();
    public final PreviewShader preview = new PreviewShader();
    public final ToonShader toon = new ToonShader();
    public final TileShader tile = new TileShader();
}
