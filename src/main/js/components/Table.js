import React from "react"
import cx from "classnames"

import { Consumer } from "./GameContainer"


class Table extends React.Component {

    state = {
        height: 0
    };

    render()
    {
        const width = 1000, height= 1000;

        const centerX = ((width / 2) | 0) - 15;
        const centerY = (height / 2) | 0;
        const tw = (width - 150) & ~1;
        const th = (height - 150) & ~1;

        return (
            <React.Fragment>
                <rect className="table"
                      x={centerX - tw / 2}
                      y={centerY - th / 2}
                      width={tw}
                      height={th}
                />
                <circle
                    className={cx("seat")}
                    cx={centerX}
                    cy={centerY - th / 2}
                    r={100}
                    height={100}
                />
                <circle
                    className={cx("seat")}
                    cx={centerX - tw / 2}
                    cy={centerY}
                    r={100}
                    height={100}
                />
                <circle
                    className={cx("seat")}
                    cx={centerX}
                    cy={centerY + th / 2}
                    r={100}
                />
                <circle
                    className={cx("seat")}
                    cx={centerX + tw / 2}
                    cy={centerY}
                    r={100}
                />

            </React.Fragment>
        );
    }
}

export default Table
