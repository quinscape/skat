import { nouns } from "nouns"
import adjectives from "adjectives"

function norm(s)
{
    return s.replace(/[\s_]/g, "-");
}

export default function()
{
    return (
        norm(adjectives[(Math.random() * adjectives.length)|0]) +
        "-" +
        norm(adjectives[(Math.random() * adjectives.length)|0]) +
        "-" +
        norm(nouns[(Math.random() * nouns.length)|0])
    );
}
