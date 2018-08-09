import React from "react"


class Sidebar extends React.Component {

    render()
    {
        return (
            <div
                id="game-side"
                className="col-md-3"
                style={
                    {
                        backgroundColor: "#eee"
                    }
                }
            >
                {
                    this.props.children
                }
            </div>
        )
    }
}

export default Sidebar
