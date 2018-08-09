    import React from "react"

import isEqual from "lodash.isequal"
import debounce from "lodash.debounce"
import {
    Form,
    withFormik
} from "formik"

import SymbolButton from "./SymbolButton"
import GameTypeRadios from "./GameTypeRadios"
import HandSettings from "./HandSettings"
import MultiplierRow from "./MultiplierRow"

// reverse order
export const SuitName = [  "Clubs", "Spades", "Hearts", "Diamonds"];

const NullGameValue = [
    23,
    45,
    46,
    59
];

const HandMultiplierName = [
    "",
    ", hand",
    ", hand schneider",
    ", hand schwarz",
    ", hand ouvert"
];

const DEFAULT_JACKS = [false, false, false, false];

const NO_RESULT = {
    description: "???",
    result: -1
};

export const GameType = {
    SUIT_CLUBS: 0,
    SUIT_SPADES: 1,
    SUIT_HEARTS: 2,
    SUIT_DIAMONDS: 3,
    NULL: 4,
    GRAND: 5,
    RAMSCH: 6
};


export const GameTypeName = [
    "with 1",
    "with 2",
    "with 3",
    "with 4",
    "without 1",
    "without 2",
    "without 3"
];

function filterNonZero(n)
{
    return n > 0;
}

function multiplierTerm(multiplierArray)
{
    if (multiplierArray.length === 0)
    {
        return "";
    }

    return " \u00d7 ( 1 + " + multiplierArray.filter(filterNonZero).join(" + ") + " )";
}

function reduceSum(a, b)
{
    return a + b;
}

function multiply(n, multiplierArray)
{
    return n * multiplierArray.reduce(reduceSum, 1);
}

function gameValue(description, value, multiplierArray)
{

    const result = multiply(value, multiplierArray);
    return {
        name: description,
        description: description +
            ( multiplierArray.length ? " = " + value : "") +
            multiplierTerm(multiplierArray) +
            " = " +
            result,
        result
    };
}

export function findJacks(hand)
{
    if (!hand)
    {
        return DEFAULT_JACKS;
    }

    const { cards } = hand;

    const jacks = [false, false, false, false];

    for (let i = 0; i < cards.length; i++)
    {
        const index = Math.abs(cards[i]) - 1;

        if ((index & 7) === 7)
        {
            jacks[3 - ((index >> 3) | 0)] = true;
        }
    }


    return jacks;
}

export function getJackGameType(jacks)
{
    if (!jacks)
    {
        return 0;
    }

    const withJacks = jacks[0];

    let count = withJacks ? 0 : 4;
    for (let i = 1; i < jacks.length; i++)
    {
        if (withJacks === jacks[i])
        {
            count++;
        }
        else
        {
            break;
        }
    }

    // no "without 4"
    if (count === 7)
    {
        return null;
    }

    return count;
}

export function getCalculatorDefaults(hand)
{
    if (!hand || hand.cards[0] === 33)
    {
        return null;
    }

    return {
        jacks: findJacks(hand),
        gameType: "0",
        hand: false,
        announce: "1",
        multiplier: "0",
        result: null
    };
}


class Calculator extends React.Component {

    state = {
        gameValue: null
    };

    static getDerivedStateFromProps(props, state)
    {
        const gameValue = Calculator.getGameValue(props.values);

        if (!state || !isEqual(state.gameValue))
        {
            return {
                gameValue
            }
        }

        return null;
    }

    componentDidMount()
    {
        this.updateLimit();
    }


    componentDidUpdate(prevProps, prevState)
    {
        if (!isEqual(prevProps.values, this.props.values))
        {
            this.updateLimit();
        }
    }

    updateLimit= debounce(
        () =>
        {
            console.log("Update limit");

            const { calculator, submitForm } = this.props;
            const { gameValue } = this.state;

            if (
                gameValue !== null && (
                    calculator.result !== gameValue.result
                )
            )
            {
                submitForm();
            }
        },
        100,
        {
            leading: true
        }
    );

    onJackClick = ev => {

        const { values, setFieldValue, multiplier } = this.props;

        if (!multiplier)
        {
            return;
        }

        const index = 4 - +ev.target.dataset.jack;

        //console.log("onJackClick", index);

        setFieldValue("jacks." + index, !values.jacks[index])

    };

    decreaseMultiplier = () => {
        const { values, setFieldValue } = this.props;

        const multiplier = +values.multiplier - 1;

        if (multiplier >= 0)
        {
            setFieldValue("multiplier", multiplier)
        }
    };


    increaseMultiplier = () => {
        const { values, setFieldValue } = this.props;

        const multiplier = +values.multiplier;
        setFieldValue("multiplier", isNaN(multiplier) || multiplier < 1 ? 1: multiplier + 1)
    };


    render()
    {
        const { values, multiplier } = this.props;
        const { gameValue } = this.state;


        const jackButtons = new Array(4);

        for (let i = 0; i < 4; i++)
        {
            jackButtons[i] = (
                <div
                    key={ i }
                    className="form-group col-sm-1"
                >
                    <SymbolButton
                        type="jack"
                        index={ 4 - i }
                        value={ values.jacks[i] }
                        onClick={ this.onJackClick }
                        disabled={!multiplier}
                    />
                </div>
            )
        }

        return (
            <Form className="form form-horizontal">
                <div className="form-row">
                    <div className="form-group col-sm-4">
                    </div>
                    {
                        jackButtons
                    }
                    <div className="form-group col-sm-4">
                    </div>
                </div>
                <fieldset className="form-group">
                    <div className="form-row">
                        <div className="col-sm-1"/>
                        <legend className="col-form-label col-sm-2 pt-0">Game Type</legend>
                        <div className="col-sm-4">
                            <GameTypeRadios
                                { ... this.props}
                            />
                        </div>
                        <div className="col-sm-4">
                            <HandSettings
                                { ... this.props }
                            />
                        </div>
                        <div className="col-sm-1"/>
                    </div>
                </fieldset>
                {
                    multiplier && <MultiplierRow
                        { ... this.props }
                        increaseMultiplier={ this.increaseMultiplier }
                        decreaseMultiplier={ this.decreaseMultiplier }
                    />
                }
                <hr/>
                <div className="row">
                    <div className="col-sm-1"/>
                    <div className="col-sm-10 text-lg-center text-info">
                        {
                            gameValue ? gameValue.description : "???"
                        }
                    </div>
                    <div className="col-sm-1"/>
                </div>
            </Form>

        )
    }

    static getGameValue(values, nameOnly = false)
    {
        const { jacks, gameType, hand, ouvert, announce } = values;

        const multiplier = +values.multiplier;

        const jackGameType = getJackGameType(jacks);

        const jackMultiplier = jackGameType !== null ? (jackGameType & 3) + 1 : 0;

        const handMultiplier = hand ? +announce : 0;

        if (+gameType === GameType.NULL)
        {
            return gameValue(
                "Null" + (ouvert ? " ouvert" : "") + (hand ? " hand" : ""),
                NullGameValue[(ouvert << 1) + hand],
                []
            );
        }
        else if (jackMultiplier > 0 && +gameType === GameType.GRAND)
        {
            return gameValue(
                " Grand " + (!nameOnly ? GameTypeName[jackGameType] : "") + HandMultiplierName[handMultiplier],
                24,
                [jackMultiplier, handMultiplier, multiplier ]
            );
        }
        else if (jackMultiplier > 0 )
        {
            const suit = +gameType;

//            console.log("Suit Game, suit = ", suit);

            return gameValue(
                SuitName[suit] + " " + (!nameOnly ? GameTypeName[jackGameType] : "") + HandMultiplierName[handMultiplier],
                12 - suit,
                [ jackMultiplier, handMultiplier, multiplier ]
            );
        }
        return NO_RESULT;
    }
}

/////////////////////////////////////////////////////////////////////////////
// formik enhancement

export function convertFormValues(gameValue, values)
{
    return {
        jacks: values.jacks,
        gameType: !values.gameType ? null : +values.gameType,
        hand: values.hand,
        ouvert: values.ouvert,
        announce: +values.announce,
        multiplier: +values.multiplier,
        result: gameValue.result
    };
}

export default withFormik({
    // Transform outer props into form values
    mapPropsToValues: ({ calculator }) => calculator,
    isInitialValid:  true,
    validate: (values, props) => {
        const errors = {  };

        const multiplier = +values.multiplier;
        if (isNaN(multiplier) || multiplier < 0)
        {
            return {
                multiplier: "Invalid multiplier"
            }
        }

        return errors;
    },
    //Submission handler
    handleSubmit:
        ( values, { props } ) => {

            const gameValue = Calculator.getGameValue(values);

            //console.log("handleSubmit", gameValue);

            if (gameValue.result > 0)
            {
                const settings = convertFormValues(gameValue, values);

                props.onSubmit(settings);
            }
        },
})(Calculator);

