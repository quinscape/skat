import React from "react"
import devicePixelRatio from "../util/devicePixelRatio";


function loadImage(uri, onLoadImage)
{
    //console.log("loadImage", uri);

    const img = document.createElement("img");
    img.className = "preload";
    img.src = uri;
    img.addEventListener("load", onLoadImage, false);

    document.body.appendChild(img);
}

function loadRecursive(images, onLoadImage, count)
{
    if (images && typeof images.length === "number")
    {
        for (let i = 0; i < images.length; i++)
        {
            const nameOrObject = images[i];

            if (typeof nameOrObject === "string")
            {
                loadImage(nameOrObject, onLoadImage);
                count++;
            }
            else
            {
                count = loadRecursive(nameOrObject, onLoadImage, count);
            }
        }
    }
    else
    {
        for (let name in images)
        {
            if (images.hasOwnProperty(name))
            {
                const nameOrObject = images[name];

                if (typeof nameOrObject === "string")
                {
                    loadImage(nameOrObject, onLoadImage);
                    count++;
                }
                else
                {
                    count = loadRecursive(nameOrObject, onLoadImage, count);
                }
            }
        }
    }

    return count;
}

class ResourceLoader extends React.Component {

    state = {
        count: 0,
        done: -1
    };
    componentDidMount()
    {
        const { images } = this.props;
        const count = loadRecursive(images, this.onLoadImage, 0);

        this.setState({
            count,
            done: 0
        })
    }

    onLoadImage = ev => {

        const { count } = this.state;

        //console.log("onLoadImage", ev.target);

        this.setState({
            done : count + 1,
        })
    };

    render()
    {
        const { children } = this.props;
        const { count, done } = this.state;

        //console.log({ count, done });
        if (done === count)
        {
            return (
                <p className="resource-throbber text-md-center">Loading...</p>
            );
        }

        return (
            children
        );
    }
}



export default ResourceLoader
