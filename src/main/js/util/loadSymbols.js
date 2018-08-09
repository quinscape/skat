import loadSVG from "./loadSVG";


const SYMBOL_RE =/^symbol:(.*?)$/;
const CONTAINER_ID = "svg-loader-container";

function transformChain(group)
{
    let transform = "";

    while (group && group.tagName !== "svg")
    {
        const t = group.getAttribute("transform");
        if (t)
        {
            transform += " " + t;
        }
        group = group.parentNode;
    }

    return transform;
}

function cleanupRecursive(group)
{
    let node = group.firstChild;

    while (node)
    {
        if (node.nodeType === 1)
        {
            const attrNames = node.getAttributeNames();

            let removed = false;
            for (let i = 0; i < attrNames.length; i++)
            {
                const name = attrNames[i];
                if (name.indexOf("inkscape:") === 0 || name.indexOf("sodipodi:") === 0)
                {
                    node.removeAttribute(name);
                    removed = true;
                }
            }

            cleanupRecursive(node);
        }
        node = node.nextSibling;
    }
}

function cleanup(group)
{
    if (typeof group.getAttributeNames === "function")
    {
        cleanupRecursive(group);
    }
}



export default function(uri) {
    return loadSVG(uri).then(doc => {

        const div = document.createElement("div");
        div.id = CONTAINER_ID;
        div.className = "preload";

        const svgContainer = doc.documentElement;
        div.appendChild(svgContainer);
        document.body.appendChild(div);

        const symbols = {};

        Array.prototype.map.call(div.getElementsByTagName("g"), group => {

            const id = group.getAttribute("id");

            const m = SYMBOL_RE.exec(id);


            if (m)
            {

                const aabb = group.getBoundingClientRect();

                cleanup(group);

                const name = m[1];
                //console.log("SYMBOL", name, aabb);
                symbols[name] = {
                    snippet: { __html : group.innerHTML },
                    aabb: {
                        x: aabb.left + 10000,
                        y: aabb.top,
                        width: aabb.width,
                        height: aabb.height
                    },
                    transform: transformChain(group) || ""
                };
            }
        });

        //document.body.removeChild(div);

        return symbols;
    })
    .catch(err => console.error("ERROR", err));
}
