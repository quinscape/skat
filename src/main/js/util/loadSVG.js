import config from "../services/config"

const parser = new DOMParser();

const SVG_MEDIA_TYPE = "image/svg+xml";

export default function(uri)
{
    const { contextPath } = config();

    return fetch(
        window.location.origin + contextPath + uri,
        {
            method: "GET",
            credentials: "same-origin",
            headers: {
                "Content-Type": SVG_MEDIA_TYPE,
            }
        }
    )
    .then(response => response.text(), err => console.error("Error loading SVG", err))
    .then(svgXml => {
        return parser.parseFromString(svgXml, SVG_MEDIA_TYPE);
    })

}
