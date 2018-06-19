package pt.neticle.ark.ssrhtml;

import java.io.IOException;

public interface Renderable
{
    void render (Appendable out) throws IOException;
}
