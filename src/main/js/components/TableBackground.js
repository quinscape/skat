import React from "react"


class TableBackground extends React.Component {

    shouldComponentUpdate()
    {
        // just a wrapper around static SVG
        return false;
    }

    render()
    {
        return (
            <React.Fragment>
                <g id="layer1" fillRule="evenodd">
                    <path
                        d="M244.00164 78.244308C244.00164 78.244308 214.99204 118.324554 214.99204 118.324554C214.99204 118.324554 215.01684 139.159964 215.01684 139.159964C215.01684 139.159964 242.04827 139.159964 242.04827 139.159964C242.04827 139.159964 244.00165 78.244308 244.00165 78.244308"
                        id="table-right" strokeLinejoin="round" strokeWidth="0.025" fill="#8d8d8d"
                    />
                    <path
                        d="M9.0971189 118.324554C9.0971189 118.324554 9.0971189 139.159964 9.0971189 139.159964C9.0971189 139.159964 214.99204 139.159964 214.99204 139.159964C214.99204 139.159964 214.99204 118.324554 214.99204 118.324554C214.99204 118.324554 9.0971189 118.324554 9.0971189 118.324554"
                        id="table-front" strokeLinejoin="round" strokeWidth="0.025" fill="#a8a8a8"
                    />
                    <path id="table-top"
                          d="M214.99206 118.324584C214.99206 118.324584 9.0968975 118.324584 9.0968975 118.324584C9.0968975 118.324584 78.540663 78.244554 78.540663 78.244554C78.540663 78.244554 244.00188 78.244554 244.00188 78.244554C244.00188 78.244554 214.99206 118.324584 214.99206 118.324584"
                          strokeLinejoin="round" stroke="#24b31c" strokeWidth="0.025" fill="#3a9d34"
                    />
                    <circle cy="90.305" cx="113.146" id="seat-0" style={{
                        marker: "none",
                        mixBlendMode:
                        "normal",
                        isolation: "auto"
                    }} r="34" color="#000" stroke="#000" strokeWidth="1.323"
                            fill="#d6d6d6" overflow="visible"/>
                    <circle style={{
                        marker: "none",
                        mixBlendMode: "normal",
                        isolation: "auto"
                    }}
                            id="seat-1" cx="35.616" cy="60.322" r="32" color="#000" stroke="#000" strokeWidth="1.058"
                            fill="#c6c6c6" overflow="visible"/>
                    <circle cy="35.851" cx="155.828" id="seat-2" style={{
                        marker: "none",
                        mixBlendMode:
                        "normal",
                        isolation: "auto"
                    }} r="30" color="#000" stroke="#000" strokeWidth="0.794"
                            fill="#afafaf" overflow="visible"/>
                    <circle style={{
                        marker: "none",
                        mixBlendMode: "normal",
                        isolation: "auto",
                        fontVariantEast_asian: "normal"
                    }} id="seat-3" cx="234.545" cy="65.276" r="32"
                            color="#000" stroke="#000" strokeWidth="1.058" fill="#c6c6c6" overflow="visible"
                    />
                </g>
            </React.Fragment>
        )
    }
}

export default TableBackground
