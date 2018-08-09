import React from "react"
import cx from "classnames"

export default function HandSettings(props)
{
    const { values, handleChange, handleBlur } = props;

    const { gameType } = values;

    return (
        <React.Fragment>
            <div className="form-check">
                <input
                    id="announce-hand"
                    className="form-check-input"
                    type="checkbox"
                    name="hand"
                    onChange={ handleChange }
                    onBlur={ handleBlur }
                />
                <label
                    className="form-check-label"
                    htmlFor="announce-hand"
                >
                    { gameType === "7" ? "Null Hand" : "Hand" }
                </label>
            </div>
            <div
                className={
                    cx(
                        "form-check",
                        gameType !== "7" && "d-none"
                    )
                }
            >
                <input
                    id="null-ouvert"
                    className="form-check-input"
                    type="checkbox"
                    name="ouvert"
                    onChange={ handleChange }
                    onBlur={ handleBlur }
                />
                <label
                    className="form-check-label"
                    htmlFor="null-ouvert"
                >
                    Null Ouvert
                </label>
            </div>
            <div
                className={
                    cx(
                        "form-check",
                        gameType === "7" && "d-none"
                    )
                }
            >
                <input
                    id="announce-nothing"
                    className="form-check-input"
                    type="radio"
                    name="announce"
                    value={ "0" }
                    checked={ values.hand && values.announce === "0" }
                    disabled={ !values.hand }
                    onChange={ handleChange }
                    onBlur={ handleBlur }
                />
                <label
                    className="form-check-label"
                    htmlFor="announce-nothing"
                >
                    Hand only
                </label>
            </div>
            <div
                className={
                    cx(
                        "form-check",
                        gameType === "7" && "d-none"
                    )
                }
            >
                <input
                    id="announce-schneider"
                    className="form-check-input"
                    type="radio"
                    name="announce"
                    value={ "1" }
                    checked={ values.hand && values.announce === "1" }
                    disabled={ !values.hand }
                    onChange={ handleChange }
                    onBlur={ handleBlur }
                />
                <label
                    className="form-check-label"
                    htmlFor="announce-schneider"
                >
                    Announce 'Schneider'
                </label>
            </div>
            <div
                className={
                    cx(
                        "form-check",
                        gameType === "7" && "d-none"
                    )
                }
            >
                <input
                    id="announce-no-tricks"
                    className="form-check-input"
                    type="radio"
                    name="announce"
                    value={ "2" }
                    checked={ values.hand && values.announce === "2" }
                    disabled={ !values.hand }
                    onChange={ handleChange }
                    onBlur={ handleBlur }
                />
                <label
                    className="form-check-label"
                    htmlFor="announce-no-tricks"
                >
                    Announce 'No Tricks'
                </label>
            </div>
            <div
                className={
                    cx(
                        "form-check",
                        gameType === "7" && "d-none"
                    )
                }
            >
                <input
                    id="announce-ouvert"
                    className="form-check-input"
                    type="radio"
                    name="announce"
                    value={ "3" }
                    checked={ values.hand && values.announce === "3" }
                    disabled={ !values.hand }
                    onChange={ handleChange }
                    onBlur={ handleBlur }
                />
                <label
                    className="form-check-label"
                    htmlFor="announce-ouvert"
                >
                    Announce 'Ouvert'
                </label>
            </div>
        </React.Fragment>
    )
}
